package com.example.iam.dto;

import java.time.Instant;

public record ApiResponse<T>(
        int status,
        T data,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(int status, T data) {
        return new ApiResponse<>(status, data, Instant.now());
    }

    public static <T> ApiResponse<T> error(int status, T data) {
        return new ApiResponse<>(status, data, Instant.now());
    }
}