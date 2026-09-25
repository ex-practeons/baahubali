package com.example.testservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ApiErrorResponse(boolean success, int status, String message, ErrorPayload error, Meta meta) {
    public ApiErrorResponse(int status, String message, String code, List<?> details) {
        this(false, status, message, new ErrorPayload(code, details),
                new Meta(Instant.now().toString(), "err-" + UUID.randomUUID()));
    }

    public record ErrorPayload(String code, List<?> details) {}
    public record Meta(String timestamp, @JsonProperty("trace_id") String traceId) {}
}
