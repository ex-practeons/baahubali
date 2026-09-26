package com.example.attemptservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        ApiError error,
        Map<String, Object> meta) {

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, status, message, data, null,
                Map.of("timestamp", Instant.now().toString()));
    }

    public static ApiResponse<Void> failure(int status, String message, ApiError error) {
        return new ApiResponse<>(false, status, message, null, error,
                Map.of("timestamp", Instant.now().toString(), "trace_id", "err-" + java.util.UUID.randomUUID()));
    }

    public record ApiError(String code, Object details) { }
}
