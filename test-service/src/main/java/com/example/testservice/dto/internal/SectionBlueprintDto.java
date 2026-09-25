package com.example.testservice.dto.internal;

import java.util.List;
import java.util.UUID;

public record SectionBlueprintDto(
        UUID sectionId,
        String title,
        Integer sequenceOrder,
        Integer durationMinutes,
        boolean shuffleQuestions,
        List<QuestionBlueprintDto> questions
) {}