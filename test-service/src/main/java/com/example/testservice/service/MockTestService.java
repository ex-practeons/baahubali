package com.example.testservice.service;

import com.example.testservice.dto.MockTestDto;
import com.example.testservice.entity.MockTest;
import com.example.testservice.entity.Question;
import com.example.testservice.entity.Status;
import com.example.testservice.entity.TestSeries;
import com.example.testservice.exception.ResourceConflictException;
import com.example.testservice.exception.ResourceNotFoundException;
import com.example.testservice.exception.ValidationException;
import com.example.testservice.repository.MockTestRepository;
import com.example.testservice.repository.QuestionRepository;
import com.example.testservice.repository.TestSeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MockTestService {
    private final MockTestRepository mockTestRepository;
    private final TestSeriesRepository testSeriesRepository;
    private final QuestionRepository questionRepository;

    public MockTestDto createMockTest(MockTestDto request) {
        TestSeries series = testSeriesRepository.findById(request.getSeriesId())
                .orElseThrow(() -> new ResourceNotFoundException("Test Series not found"));

        if (series.getCategory() != null) {
            LanguageValidationUtil.validateTranslationsMap(request.getTitleTranslations(), series.getCategory().getRequiredLanguages());
        }

        if (request.getDurationMinutes() <= 0) {
            throw new ValidationException("Duration minutes must be > 0");
        }

        MockTest mockTest = MockTest.builder()
                .id(UUID.randomUUID().toString())
                .series(series)
                .titleTranslations(request.getTitleTranslations())
                .durationMinutes(request.getDurationMinutes())
                .totalMarks(request.getTotalMarks())
                .difficulty(request.getDifficulty())
                .isSectionOrderStrict(request.getIsSectionOrderStrict())
                .shuffleSections(request.getShuffleSections())
                .status(Status.DRAFT)
                .build();
        return mapToDto(mockTestRepository.save(mockTest));
    }

    public MockTestDto updateMockTest(String id, MockTestDto request) {
        MockTest mockTest = mockTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mock Test not found"));

        if (mockTest.getSeries().getCategory() != null) {
            LanguageValidationUtil.validateTranslationsMap(request.getTitleTranslations(), mockTest.getSeries().getCategory().getRequiredLanguages());
        }

        if (request.getDurationMinutes() <= 0) {
            throw new ValidationException("Duration minutes must be > 0");
        }

        mockTest.setTitleTranslations(request.getTitleTranslations());
        mockTest.setDurationMinutes(request.getDurationMinutes());
        mockTest.setTotalMarks(request.getTotalMarks());
        mockTest.setDifficulty(request.getDifficulty());
        mockTest.setIsSectionOrderStrict(request.getIsSectionOrderStrict());
        mockTest.setShuffleSections(request.getShuffleSections());
        return mapToDto(mockTestRepository.save(mockTest));
    }

    public void deleteMockTest(String id) {
        MockTest mockTest = mockTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mock Test not found"));
        if (mockTest.getStatus() == Status.PUBLISHED) {
            throw new ResourceConflictException("Cannot delete a published mock test");
        }
        mockTest.setDeletedAt(Instant.now());
        mockTestRepository.save(mockTest);
    }

    public MockTestDto publish(String id) {
        MockTest mockTest = mockTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mock Test not found"));

        List<Question> questions = questionRepository.findByTestIdAndDeletedAtIsNull(mockTest.getId());
        BigDecimal totalPositive = questions.stream()
                .map(Question::getPositiveMarks)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPositive.compareTo(new BigDecimal(mockTest.getTotalMarks())) != 0) {
            throw new ValidationException("Total marks (" + mockTest.getTotalMarks() + 
                    ") do not match the sum of positive marks of questions (" + totalPositive + ").");
        }

        // Validate all required languages are present in all questions
        java.util.List<String> requiredLanguages = mockTest.getSeries().getCategory() != null ? 
                mockTest.getSeries().getCategory().getRequiredLanguages() : java.util.Collections.emptyList();
        
        if (!requiredLanguages.isEmpty()) {
            java.util.List<String> errors = new java.util.ArrayList<>();
            for (Question q : questions) {
                java.util.Set<String> qLangs = q.getTranslations().stream()
                        .map(com.example.testservice.entity.QuestionTranslation::getLanguage)
                        .collect(Collectors.toSet());
                for (String reqLang : requiredLanguages) {
                    if (!qLangs.contains(reqLang)) {
                        errors.add("Question " + q.getId() + " is missing translation for " + reqLang);
                    }
                }
            }
            if (!errors.isEmpty()) {
                throw new ValidationException("Publish failed due to missing translations: " + String.join("; ", errors));
            }
        }

        mockTest.setStatus(Status.PUBLISHED);
        return mapToDto(mockTestRepository.save(mockTest));
    }

    public com.example.testservice.dto.catalog.MockTestResponseDto getMockTestById(String id, String lang) {
        MockTest mockTest = mockTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mock Test not found"));
        return mapToCatalogDto(mockTest, lang);
    }

    public List<com.example.testservice.dto.catalog.MockTestResponseDto> getMockTestsBySeries(String seriesId, String lang) {
        return mockTestRepository.findBySeriesIdAndDeletedAtIsNull(seriesId).stream()
                .map(mt -> mapToCatalogDto(mt, lang))
                .collect(Collectors.toList());
    }

    private MockTestDto mapToDto(MockTest mockTest) {
        return MockTestDto.builder()
                .id(mockTest.getId())
                .seriesId(mockTest.getSeries().getId())
                .titleTranslations(mockTest.getTitleTranslations())
                .durationMinutes(mockTest.getDurationMinutes())
                .totalMarks(mockTest.getTotalMarks())
                .difficulty(mockTest.getDifficulty())
                .isSectionOrderStrict(mockTest.getIsSectionOrderStrict())
                .shuffleSections(mockTest.getShuffleSections())
                .status(mockTest.getStatus())
                .build();
    }

    private com.example.testservice.dto.catalog.MockTestResponseDto mapToCatalogDto(MockTest mockTest, String requestedLang) {
        java.util.List<String> required = mockTest.getSeries().getCategory() != null ? mockTest.getSeries().getCategory().getRequiredLanguages() : java.util.Collections.emptyList();
        String lang = LanguageValidationUtil.resolveLanguage(requestedLang, required);
        String title = mockTest.getTitleTranslations().get(lang);
        if (title == null) {
            throw new ResourceNotFoundException("Translation not found for language: " + lang);
        }
        return com.example.testservice.dto.catalog.MockTestResponseDto.builder()
                .id(mockTest.getId())
                .seriesId(mockTest.getSeries().getId())
                .title(title)
                .durationMinutes(mockTest.getDurationMinutes())
                .totalMarks(mockTest.getTotalMarks())
                .difficulty(mockTest.getDifficulty())
                .requiredLanguages(required)
                .build();
    }
}
