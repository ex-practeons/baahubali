package com.example.apigateway.exception;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ErrorResponse(
        boolean success,
        int status,
        String message,
        Error error,
        Meta meta) {

    public static ErrorResponse of(AuthErrorCode errorCode) {
        return new ErrorResponse(
                false,
                errorCode.getStatus().value(),
                errorCode.getClientMessage(),
                new Error(errorCode.name(), List.of()),
                new Meta(Instant.now().toString(), "err-" + UUID.randomUUID()));
    }

    public record Error(String code, List<?> details) {}

    // This component name preserves the API contract's snake_case JSON key.
    public record Meta(String timestamp, String trace_id) {}
}
