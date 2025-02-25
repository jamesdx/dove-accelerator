package com.doveaccelerator.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        String timestamp = LocalDateTime.now().format(formatter);
        String clientIP = request.getRemoteAddress().getHostString();

        log.info("[{}] {} {} from {} - Start", timestamp, method, path, clientIP);
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                int statusCode = exchange.getResponse().getStatusCode().value();
                
                log.info("[{}] {} {} from {} - Completed {} in {}ms",
                    LocalDateTime.now().format(formatter),
                    method,
                    path,
                    clientIP,
                    statusCode,
                    duration);
            }));
    }

    @Override
    public int getOrder() {
        return -200; // Execute before authentication
    }
}