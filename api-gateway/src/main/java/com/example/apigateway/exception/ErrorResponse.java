package com.example.apigateway.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path) {

    public static ErrorResponse of(AuthErrorCode errorCode, String path) {
        HttpStatus status = errorCode.getStatus();
        return new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                errorCode.name(),
                errorCode.getClientMessage(),
                path);
    }
}