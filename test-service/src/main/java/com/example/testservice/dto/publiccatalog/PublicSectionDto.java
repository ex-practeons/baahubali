package com.example.testservice.dto.publiccatalog;

import java.util.List;
import java.util.UUID;

public record PublicSectionDto(
        UUID sectionId,
        String title,
        Integer sequenceOrder,
        List<PublicQuestionDto> questions
) {}