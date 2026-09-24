package com.example.iam.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        ApiErrorResponse error,
        Map<String, Object> meta
) {
    private static final String API_VERSION = "0.0.1-SNAPSHOT";

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, status, message, data, null, defaultMeta());
    }

    public static ApiResponse<Void> error(
            int status, String message, String code, java.util.List<ApiErrorDetail> details) {
        ApiErrorResponse error = new ApiErrorResponse(code, details);
        Map<String, Object> meta = Map.of(
                "timestamp", Instant.now().toString(),
                "trace_id", UUID.randomUUID().toString()
        );
        return new ApiResponse<>(false, status, message, null, error, meta);
    }

    private static Map<String, Object> defaultMeta() {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "version", API_VERSION
        );
    }
}
