package com.example.iam.dto;

import java.time.Instant;
import java.util.Map;

public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        Map<String, Object> meta
) {
    private static final String API_VERSION = "0.0.1-SNAPSHOT";

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, status, message, data, defaultMeta());
    }

    /**
     * Kept temporarily so existing controllers can migrate to endpoint-specific
     * messages in a separate step.
     */
    public static <T> ApiResponse<T> success(int status, T data) {
        return success(status, "Request completed successfully", data);
    }

    public static <T> ApiResponse<T> error(int status, T data) {
        return new ApiResponse<>(false, status, "Request failed", data, defaultMeta());
    }

    private static Map<String, Object> defaultMeta() {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "version", API_VERSION
        );
    }
}
