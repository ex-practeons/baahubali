package com.example.testservice.dto.catalog;

import com.example.testservice.entity.Difficulty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MockTestResponseDto {
    private String id;
    private String seriesId;
    private String title; // resolved based on requested lang
    private Integer durationMinutes;
    private Integer totalMarks;
    private Difficulty difficulty;
    private List<String> requiredLanguages;
}
