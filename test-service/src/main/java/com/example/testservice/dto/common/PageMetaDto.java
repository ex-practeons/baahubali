package com.example.testservice.dto.common;

public record PageMetaDto(
        int page,
        int limit,
        long total,
        int totalPages
) {}