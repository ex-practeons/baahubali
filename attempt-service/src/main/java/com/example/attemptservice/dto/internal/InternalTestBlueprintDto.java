package com.example.attemptservice.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalTestBlueprintDto {
    private String id;
    private String title;
    private List<InternalSectionDto> sections;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InternalSectionDto {
        private String id;
        private List<InternalQuestionDto> questions;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InternalQuestionDto {
        private String id;
        private String questionType;
        private List<InternalTranslationDto> translations;
        private Map<String, Object> correctAnswerJson;
        private String explanation;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InternalTranslationDto {
        private String language;
        private String questionText;
    }
}