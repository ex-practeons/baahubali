package com.example.testservice.dto;

import com.example.testservice.entity.Difficulty;
import com.example.testservice.entity.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuestionCreateRequest {
    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @NotEmpty(message = "Translations are required")
    @Valid
    private java.util.List<QuestionTranslationDto> translations;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    @NotNull(message = "Positive marks are required")
    private BigDecimal positiveMarks;

    private BigDecimal negativeMarks = BigDecimal.ZERO;

    private Difficulty difficulty;
}
