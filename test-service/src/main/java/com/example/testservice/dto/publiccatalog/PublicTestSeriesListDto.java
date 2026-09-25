package com.example.testservice.dto.publiccatalog;

import java.math.BigDecimal;
import java.util.UUID;

public record PublicTestSeriesListDto(
        UUID id,
        String title,
        BigDecimal basePrice,
        String categoryName
) {}
