package com.example.testservice.dto.admin;

import java.time.Instant;
import java.util.UUID;

public record AdminSectionDetailDto(
        UUID id,
        UUID testId,
        String title,
        Integer sequenceOrder,
        Integer durationMinutes,
        boolean shuffleQuestions,
        int questionCount,
        Instant createdAt,
        Instant updatedAt
) {}
