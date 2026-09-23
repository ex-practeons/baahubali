package com.example.testservice.dto.common;

import java.util.List;

public record PaginatedResponseDto<T>(
        List<T> data,
        PageMetaDto meta
) {}