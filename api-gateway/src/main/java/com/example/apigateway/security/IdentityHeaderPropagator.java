package com.example.apigateway.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class IdentityHeaderPropagator {
    public ServerHttpRequest sanitize(ServerHttpRequest request) {
        return request.mutate()
                .headers(headers -> {
                    headers.remove(GatewayHeaders.USER_ID);
                    headers.remove(GatewayHeaders.USER_ROLE);
                })
                .build();
    }

    public ServerHttpRequest propagate(ServerHttpRequest request, AuthenticatedUser user) {
        return request.mutate()
                .headers(headers -> {
                    headers.set(GatewayHeaders.USER_ID, user.userId());
                    headers.set(GatewayHeaders.USER_ROLE, user.role());
                })
                .build();
    }
}