package com.example.testservice.dto.catalog;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryResponseDto {
    private String id;
    private String name;
    private String description;
    private List<String> requiredLanguages;
}
