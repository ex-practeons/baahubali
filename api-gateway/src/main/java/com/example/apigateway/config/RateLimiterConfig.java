package com.example.apigateway.config;

import java.net.InetSocketAddress;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    public static final String RATE_LIMIT_SUBJECT_ATTR = "gateway.rateLimit.subject";

    @Bean
    public KeyResolver userOrIpKeyResolver() {
        return exchange -> Mono.just(resolveRouteId(exchange) + ":" + resolveSubject(exchange));
    }

    private String resolveSubject(ServerWebExchange exchange) {
        String userId = exchange.getAttribute(RATE_LIMIT_SUBJECT_ATTR);
        if (userId != null && !userId.isBlank()) {
            return "user:" + userId;
        }
        return "ip:" + resolveClientIp(exchange);
    }

    private String resolveClientIp(ServerWebExchange exchange) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress == null || remoteAddress.getAddress() == null) {
            return "unknown";
        }
        return remoteAddress.getAddress().getHostAddress();
    }

    private String resolveRouteId(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        return route != null ? route.getId() : "unmatched-route";
    }
}