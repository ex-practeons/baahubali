package com.example.testservice.dto.admin;

import java.math.BigDecimal;
import java.util.UUID;

public record TestSeriesCreateDto(
        String title,
        BigDecimal basePrice,
        UUID categoryId
) {}