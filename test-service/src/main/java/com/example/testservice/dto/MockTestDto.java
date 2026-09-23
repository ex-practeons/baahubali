package com.example.testservice.dto;

import com.example.testservice.entity.Difficulty;
import com.example.testservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockTestDto {
    private String id;
    private String seriesId;
    private java.util.Map<String, String> titleTranslations;
    private Integer durationMinutes;
    private Integer totalMarks;
    private Difficulty difficulty;
    private Boolean isSectionOrderStrict;
    private Boolean shuffleSections;
    private Status status;
}
