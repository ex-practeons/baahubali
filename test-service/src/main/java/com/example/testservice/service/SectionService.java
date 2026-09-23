package com.example.testservice.service;

import com.example.testservice.dto.SectionDto;
import com.example.testservice.entity.MockTest;
import com.example.testservice.entity.Section;
import com.example.testservice.exception.ResourceNotFoundException;
import com.example.testservice.repository.MockTestRepository;
import com.example.testservice.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final MockTestRepository mockTestRepository;

    public SectionDto createSection(String testId, SectionDto request) {
        MockTest mockTest = mockTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock Test not found"));

        if (mockTest.getSeries().getCategory() != null) {
            LanguageValidationUtil.validateTranslationsMap(request.getTitleTranslations(), mockTest.getSeries().getCategory().getRequiredLanguages());
        }

        Section section = Section.builder()
                .id(UUID.randomUUID().toString())
                .test(mockTest)
                .titleTranslations(request.getTitleTranslations())
                .sequenceOrder(request.getSequenceOrder())
                .shuffleQuestions(request.getShuffleQuestions())
                .build();
        return mapToDto(sectionRepository.save(section));
    }

    public List<SectionDto> getSectionsByTest(String testId) {
        return sectionRepository.findByTestIdAndDeletedAtIsNullOrderBySequenceOrderAsc(testId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private SectionDto mapToDto(Section section) {
        return SectionDto.builder()
                .id(section.getId())
                .testId(section.getTest().getId())
                .titleTranslations(section.getTitleTranslations())
                .sequenceOrder(section.getSequenceOrder())
                .shuffleQuestions(section.getShuffleQuestions())
                .build();
    }
}
