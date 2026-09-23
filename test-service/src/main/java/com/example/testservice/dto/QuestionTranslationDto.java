package com.example.testservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionTranslationDto {
    @NotBlank(message = "Language is required")
    private String language;

    @NotBlank(message = "Question text is required")
    private String questionText;

    private String optionsJson;
}
