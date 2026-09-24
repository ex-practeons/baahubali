package com.example.iam.dto;

public record ApiErrorDetail(
        String field,
        String issue
) {}
