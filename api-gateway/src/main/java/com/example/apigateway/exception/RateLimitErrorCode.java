package com.example.apigateway.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RateLimitErrorCode implements GatewayErrorCode {

    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Too many requests. Please slow down and try again shortly.");

    private final HttpStatus status;
    private final String clientMessage;
}