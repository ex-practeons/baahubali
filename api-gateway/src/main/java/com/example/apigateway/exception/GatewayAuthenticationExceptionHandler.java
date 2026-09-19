package com.example.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2) 
public class GatewayAuthenticationExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayAuthenticationExceptionHandler.class);

    private final ObjectMapper objectMapper;

    public GatewayAuthenticationExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        if (!(throwable instanceof GatewayAuthenticationException exception)) {
            return Mono.error(throwable);
        }

        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(throwable);
        }

        AuthErrorCode errorCode = exception.getErrorCode();
        String path = exchange.getRequest().getPath().value();

        log.debug("Authentication rejected [{} {}] code={} reason={}",
                exchange.getRequest().getMethod(), path, errorCode, exception.getMessage());

        response.setStatusCode(errorCode.getStatus());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(
                Mono.fromCallable(() -> serialize(ErrorResponse.of(errorCode, path), response.bufferFactory())));
    }

    private DataBuffer serialize(ErrorResponse body, DataBufferFactory bufferFactory) throws JsonProcessingException {
        return bufferFactory.wrap(objectMapper.writeValueAsBytes(body));
    }
}
