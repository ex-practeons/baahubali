package com.example.testservice.dto.admin;

import com.example.testservice.dto.QuestionTranslationDto;
import com.example.testservice.entity.Difficulty;
import com.example.testservice.entity.QuestionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record QuestionDetailDto(
        UUID id,
        QuestionType questionType,
        List<QuestionTranslationDto> translations,
        Map<String, Object> correctAnswerJson,
        BigDecimal positiveMarks,
        BigDecimal negativeMarks,
        String explanation,
        Difficulty difficulty,
        boolean isLocked,
        Instant createdAt,
        String createdBy
) {}