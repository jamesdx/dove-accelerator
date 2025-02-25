package com.doveaccelerator.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes
                .route("auth-service", r -> r.path("/dove-auth/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
                        .uri("lb://dove-auth"))
                
                // Agent Service Routes
                .route("agent-service", r -> r.path("/dove-agent/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
                        .uri("lb://dove-agent"))
                
                // Project Service Routes
                .route("project-service", r -> r.path("/dove-project/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
                        .uri("lb://dove-project"))
                
                // Task Service Routes
                .route("task-service", r -> r.path("/dove-task/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
                        .uri("lb://dove-task"))
                
                // Knowledge Service Routes
                .route("knowledge-service", r -> r.path("/dove-knowledge/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
                        .uri("lb://dove-knowledge"))
                .build();
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }
}