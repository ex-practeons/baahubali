package com.example.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.example.apigateway.exception.MissingTokenException;
import com.example.apigateway.security.IdentityHeaderPropagator;
import com.example.apigateway.security.PublicRouteMatcher;
import com.example.apigateway.security.TokenExtractor;
import com.example.apigateway.security.TokenValidator;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    public static final int ORDER = -100;

    private final PublicRouteMatcher publicRouteMatcher;
    private final TokenExtractor tokenExtractor;
    private final TokenValidator tokenValidator;
    private final IdentityHeaderPropagator identityHeaderPropagator;

    public JwtAuthenticationGlobalFilter(PublicRouteMatcher publicRouteMatcher,
                                         TokenExtractor tokenExtractor,
                                         TokenValidator tokenValidator,
                                         IdentityHeaderPropagator identityHeaderPropagator) {
        this.publicRouteMatcher = publicRouteMatcher;
        this.tokenExtractor = tokenExtractor;
        this.tokenValidator = tokenValidator;
        this.identityHeaderPropagator = identityHeaderPropagator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (publicRouteMatcher.isPublic(request)) {
            return forward(exchange, chain, identityHeaderPropagator.sanitize(request));
        }

        String token = tokenExtractor.extract(request).orElse(null);
        if (token == null) {
            return Mono.error(new MissingTokenException());
        }

        return tokenValidator.validate(token)
                .map(user -> identityHeaderPropagator.propagate(request, user))
                .flatMap(authenticatedRequest -> forward(exchange, chain, authenticatedRequest));
    }

    private Mono<Void> forward(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request) {
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}