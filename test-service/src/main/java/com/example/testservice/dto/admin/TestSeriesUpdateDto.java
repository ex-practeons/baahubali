package com.example.testservice.dto.admin;

import java.math.BigDecimal;
import java.util.UUID;

public record TestSeriesUpdateDto(
        String title,
        BigDecimal basePrice,
        UUID categoryId
) {}