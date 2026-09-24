package com.example.iam.dto;

import java.util.List;

public record ApiErrorResponse(
        String code,
        List<ApiErrorDetail> details
) {}
