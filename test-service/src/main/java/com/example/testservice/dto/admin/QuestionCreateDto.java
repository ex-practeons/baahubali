package com.example.testservice.dto.admin;

import com.example.testservice.dto.QuestionTranslationDto;
import com.example.testservice.entity.Difficulty;
import com.example.testservice.entity.QuestionType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record QuestionCreateDto(
        QuestionType questionType,
        List<QuestionTranslationDto> translations,
        Map<String, Object> correctAnswerJson,
        BigDecimal positiveMarks,
        BigDecimal negativeMarks,
        String explanation,
        Difficulty difficulty
) {}