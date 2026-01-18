package com.pubg.smp.smpgateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 适配 Spring Boot 3.2 的网关全局异常处理器
 */
@Slf4j
@Component
@Order(-1) // 优先级设为最高，覆盖系统默认的异常处理
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "服务器内部错误";

        // 针对特定的网关异常进行分类
        if (ex instanceof org.springframework.web.server.ResponseStatusException) {
            status = (HttpStatus) ((org.springframework.web.server.ResponseStatusException) ex).getStatusCode();
            message = status.getReasonPhrase();
        } else if (ex instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = "找不到目标服务";
        }

        ServerHttpResponse response = exchange.getResponse();

        // 如果响应已经提交（比如已经发了一部分数据），直接返回错误信号
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        // 构建统一返回格式
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("msg", ex.getMessage() != null ? ex.getMessage() : "网关服务异常");
        result.put("data", "");

        log.error("[网关异常] 路径: {}, 原因: {}", exchange.getRequest().getPath(), ex.getMessage());

        return response.writeWith(Mono.fromSupplier(() -> {
            DataBuffer buffer = null;
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(result);
                buffer = response.bufferFactory().wrap(bytes);
            } catch (JsonProcessingException e) {
                log.error("JSON 序列化失败", e);
            }
            return buffer;
        }));
    }
}