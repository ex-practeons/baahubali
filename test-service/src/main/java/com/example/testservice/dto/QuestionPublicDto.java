package com.example.testservice.dto;

import com.example.testservice.entity.QuestionType;
import com.example.testservice.entity.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPublicDto {
    private String id;
    private QuestionType questionType;
    private String questionText;
    private Map<String, Object> options;
    private BigDecimal positiveMarks;
    private BigDecimal negativeMarks;
    private Difficulty difficulty;
}
