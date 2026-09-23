package com.example.testservice.dto.internal;

public record QuestionTranslationBlueprintDto(
        String language,
        String questionText,
        String optionsJson
) {}