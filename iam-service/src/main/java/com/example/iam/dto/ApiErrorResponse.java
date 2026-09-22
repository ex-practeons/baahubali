package com.example.iam.dto;

import java.util.List;

public record ApiErrorResponse(
        String error,
        String message,
        String path,
        List<String> details
) {}