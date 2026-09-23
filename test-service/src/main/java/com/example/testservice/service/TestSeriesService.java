package com.example.testservice.service;

import com.example.testservice.dto.TestSeriesDto;
import com.example.testservice.entity.Category;
import com.example.testservice.entity.MockTest;
import com.example.testservice.entity.Status;
import com.example.testservice.entity.TestSeries;
import com.example.testservice.exception.ResourceConflictException;
import com.example.testservice.exception.ResourceNotFoundException;
import com.example.testservice.repository.CategoryRepository;
import com.example.testservice.repository.MockTestRepository;
import com.example.testservice.repository.TestSeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestSeriesService {
    private final TestSeriesRepository testSeriesRepository;
    private final CategoryRepository categoryRepository;
    private final MockTestRepository mockTestRepository;

    public TestSeriesDto createTestSeries(TestSeriesDto request) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            LanguageValidationUtil.validateTranslationsMap(request.getTitleTranslations(), category.getRequiredLanguages());
        }

        TestSeries series = TestSeries.builder()
                .id(UUID.randomUUID().toString())
                .category(category)
                .titleTranslations(request.getTitleTranslations())
                .basePrice(request.getBasePrice())
                .isFree(request.getIsFree())
                .status(Status.DRAFT)
                .build();
        return mapToDto(testSeriesRepository.save(series));
    }

    public TestSeriesDto updateTestSeries(String id, TestSeriesDto request) {
        TestSeries series = testSeriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Series not found"));
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            series.setCategory(category);
            LanguageValidationUtil.validateTranslationsMap(request.getTitleTranslations(), category.getRequiredLanguages());
        }
        series.setTitleTranslations(request.getTitleTranslations());
        series.setBasePrice(request.getBasePrice());
        series.setIsFree(request.getIsFree());
        return mapToDto(testSeriesRepository.save(series));
    }

    public void deleteTestSeries(String id) {
        TestSeries series = testSeriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Series not found"));
        if (series.getStatus() == Status.PUBLISHED) {
            throw new ResourceConflictException("Cannot delete a published test series");
        }
        series.setDeletedAt(Instant.now());
        testSeriesRepository.save(series);
    }

    public TestSeriesDto publish(String id) {
        TestSeries series = testSeriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Series not found"));
        series.setStatus(Status.PUBLISHED);
        return mapToDto(testSeriesRepository.save(series));
    }

    public TestSeriesDto unpublish(String id) {
        TestSeries series = testSeriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Series not found"));
        series.setStatus(Status.DRAFT);
        return mapToDto(testSeriesRepository.save(series));
    }

    public Page<com.example.testservice.dto.catalog.TestSeriesResponseDto> getCatalog(String categoryId, Boolean isFree, String lang, Pageable pageable) {
        return testSeriesRepository.findAllByFilters(Status.PUBLISHED, categoryId, isFree, pageable)
                .map(s -> mapToCatalogDto(s, lang));
    }

    public com.example.testservice.dto.catalog.TestSeriesResponseDto getSeriesById(String id, String lang) {
        TestSeries series = testSeriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Series not found"));
        return mapToCatalogDto(series, lang);
    }

    private TestSeriesDto mapToDto(TestSeries series) {
        return TestSeriesDto.builder()
                .id(series.getId())
                .categoryId(series.getCategory() != null ? series.getCategory().getId() : null)
                .titleTranslations(series.getTitleTranslations())
                .basePrice(series.getBasePrice())
                .isFree(series.getIsFree())
                .status(series.getStatus())
                .build();
    }

    private com.example.testservice.dto.catalog.TestSeriesResponseDto mapToCatalogDto(TestSeries series, String requestedLang) {
        java.util.List<String> required = series.getCategory() != null ? series.getCategory().getRequiredLanguages() : java.util.Collections.emptyList();
        String lang = LanguageValidationUtil.resolveLanguage(requestedLang, required);
        String title = series.getTitleTranslations().get(lang);
        if (title == null) {
            throw new ResourceNotFoundException("Translation not found for language: " + lang);
        }
        return com.example.testservice.dto.catalog.TestSeriesResponseDto.builder()
                .id(series.getId())
                .categoryId(series.getCategory() != null ? series.getCategory().getId() : null)
                .title(title)
                .basePrice(series.getBasePrice())
                .isFree(series.getIsFree())
                .requiredLanguages(required)
                .build();
    }
}
