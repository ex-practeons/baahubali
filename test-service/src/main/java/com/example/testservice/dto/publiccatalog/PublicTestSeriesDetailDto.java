package com.example.testservice.dto.publiccatalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PublicTestSeriesDetailDto(
        UUID id,
        String title,
        BigDecimal basePrice,
        UUID categoryId,
        String categoryName,
        List<PublicMockTestSummaryDto> mockTests
) {
    public record PublicMockTestSummaryDto(
            UUID testId,
            String title,
            Integer durationMinutes,
            BigDecimal totalMarks,
            boolean isFree
    ) {}
}
