package com.example.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.apigateway.exception.ErrorResponse;
import com.example.apigateway.exception.RateLimitErrorCode;

import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Component
public class RateLimitResponseDecoratorFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;

    public RateLimitResponseDecoratorFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

            @Override
            public Mono<Void> setComplete() {
                if (HttpStatus.TOO_MANY_REQUESTS.equals(getStatusCode()) && !isCommitted()) {
                    getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    byte[] bytes = objectMapper.writeValueAsBytes(
                            ErrorResponse.of(RateLimitErrorCode.RATE_LIMIT_EXCEEDED));
                    DataBuffer buffer = bufferFactory().wrap(bytes);
                    return writeWith(Mono.just(buffer));
                }
                return super.setComplete();
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}