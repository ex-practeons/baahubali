package com.example.testservice.dto.internal;

import com.example.testservice.entity.Status;
import java.math.BigDecimal;
import java.util.UUID;

public record TestStatusDto(
        UUID testId,
        Status status,
        BigDecimal totalMarks,
        Integer durationMinutes
) {}