package com.example.apigateway.exception;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
        int status,
        Object data,
        Instant timestamp
) {
    public static ErrorResponse of(AuthErrorCode errorCode, String path) {
        HttpStatus status = errorCode.getStatus();
        
        Map<String, String> errorData = Map.of(
                "error", status.getReasonPhrase(),
                "code", errorCode.name(),
                "message", errorCode.getClientMessage(),
                "path", path
        );
        
        return new ErrorResponse(
                status.value(),
                errorData,
                Instant.now()
        );
    }
}