package com.pubg.smp.smpgateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubg.smp.smpgateway.entity.LoginUser;
import com.pubg.smp.smpgateway.util.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 适配 Spring Boot 3.2 + Gateway 的全域鉴权过滤器
 */
@Component
public class AuthorizationHeaderFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    // 内部接口
    private static final String INTERNAL_PATTERN = "/*/internal/**";
    // 不用鉴权的接口
    private static final String[] IGNORE_SERVER_PATH = {"/security/login", "/room/check_image", "/room/face_image"};
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 白名单检查
        for (String skipPath : IGNORE_SERVER_PATH) {
            if (path.startsWith(skipPath)) {
                return chain.filter(exchange);
            }
        }

        // 2. 内部路径屏蔽
        if (ANT_PATH_MATCHER.match(INTERNAL_PATTERN, path)) {
            return errorResponse(exchange, HttpStatus.FORBIDDEN, "禁止访问内部接口");
        }

        // 3. 获取 Token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isEmpty()) {
            return errorResponse(exchange, HttpStatus.UNAUTHORIZED, "请先登录");
        }

        try {
            // 4. 解析 JWT
            LoginUser loginUser = JwtUtils.getLoginUser(authHeader);
//            System.out.println(loginUser.getRole().getId());
            // 5. 信息透传 (Gateway 中建议使用 Header 传递，而不是修改 Query 参数)
            // 获取原始 URI
            URI uri = exchange.getRequest().getURI();
            // 使用 UriComponentsBuilder 动态拼接参数
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(uri);
            uriBuilder.queryParam("username", loginUser.getUsername())
                    .queryParam("email", loginUser.getEmail())
                    .queryParam("tel", loginUser.getTel())
                    .queryParam("roleId", loginUser.getRole().getId());

            // 5. 构建新的请求对象
            URI newUri = uriBuilder.build(true).toUri(); // build(true) 表示已经编码过了
            ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(newUri).build();

            // 6. 放行
            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (Exception e) {
            return errorResponse(exchange, HttpStatus.UNAUTHORIZED, "登录已失效");
        }
    }

    // 统一错误处理 (响应式写法)
    private Mono<Void> errorResponse(ServerWebExchange exchange, HttpStatus status, String msg) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> map = new HashMap<>();
        map.put("code", status.value());
        map.put("msg", msg);
        map.put("data", "");

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(map);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1; // 优先级最高
    }
}