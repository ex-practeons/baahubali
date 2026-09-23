package com.example.testservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDto {
    private String id;
    private String testId;
    private java.util.Map<String, String> titleTranslations;
    private Integer sequenceOrder;
    private Boolean shuffleQuestions;
}
