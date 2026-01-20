package com.pubg.smp.smpinfo.config; // 记得改包名

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP 请求追踪配置 (适配 Spring Boot 3.2)
 * 原 HttpTraceRepository 已被移除，现改为 HttpExchangeRepository
 * @author itning
 */
@Configuration
public class HttpTraceActuatorConfiguration {

    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        // 默认记录最近 100 条请求
        return new InMemoryHttpExchangeRepository();
    }
}