package com.example.testservice.dto;

import com.example.testservice.entity.QuestionType;
import com.example.testservice.entity.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionInternalDto {
    private String id;
    private String sectionId;
    private QuestionType questionType;
    private String questionText;
    private String optionsJson;
    private String correctAnswer;
    private BigDecimal positiveMarks;
    private BigDecimal negativeMarks;
    private Difficulty difficulty;
    private Instant createdAt;
    private Instant updatedAt;
}
