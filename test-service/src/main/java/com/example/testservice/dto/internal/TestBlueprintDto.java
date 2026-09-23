package com.example.testservice.dto.internal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TestBlueprintDto(
        UUID testId,
        String title,
        Integer durationMinutes,
        boolean isSectionOrderStrict,
        boolean shuffleSections,
        boolean negativeMarkingEnabled,
        BigDecimal totalMarks,
        List<SectionBlueprintDto> sections
) {}