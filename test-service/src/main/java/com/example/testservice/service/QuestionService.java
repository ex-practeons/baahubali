package com.example.testservice.service;

import com.example.testservice.dto.QuestionCreateRequest;
import com.example.testservice.dto.QuestionInternalDto;
import com.example.testservice.dto.QuestionPublicDto;
import com.example.testservice.entity.Question;
import com.example.testservice.entity.QuestionType;
import com.example.testservice.entity.Section;
import com.example.testservice.entity.Status;
import com.example.testservice.exception.ResourceConflictException;
import com.example.testservice.exception.ResourceNotFoundException;
import com.example.testservice.exception.ValidationException;
import com.example.testservice.repository.QuestionRepository;
import com.example.testservice.repository.SectionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final SectionRepository sectionRepository;
    private final AttemptLockService attemptLockService;
    private final ObjectMapper objectMapper;

    public QuestionPublicDto createQuestion(String sectionId, QuestionCreateRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        if (section.getTest().getSeries().getCategory() != null) {
            java.util.Set<String> providedLangs = request.getTranslations().stream()
                    .map(com.example.testservice.dto.QuestionTranslationDto::getLanguage).collect(Collectors.toSet());
            LanguageValidationUtil.validateTranslationsSet(providedLangs, section.getTest().getSeries().getCategory().getRequiredLanguages());
        }

        validateQuestionRequest(request);

        Question question = Question.builder()
                .id(UUID.randomUUID().toString())
                .section(section)
                .questionType(request.getQuestionType())
                .correctAnswer(request.getCorrectAnswer())
                .positiveMarks(request.getPositiveMarks())
                .negativeMarks(request.getNegativeMarks() != null ? request.getNegativeMarks() : BigDecimal.ZERO)
                .difficulty(request.getDifficulty())
                .build();
        
        List<com.example.testservice.entity.QuestionTranslation> translations = request.getTranslations().stream().map(t -> 
                com.example.testservice.entity.QuestionTranslation.builder()
                        .id(UUID.randomUUID().toString())
                        .question(question)
                        .language(t.getLanguage())
                        .questionText(t.getQuestionText())
                        .optionsJson(t.getOptionsJson())
                        .build()
        ).collect(Collectors.toList());
        question.setTranslations(translations);

        return mapToPublicDto(questionRepository.save(question), null);
    }

    public QuestionPublicDto updateQuestion(String id, QuestionCreateRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        String testId = question.getSection().getTest().getId();
        Status testStatus = question.getSection().getTest().getStatus();
        
        if (testStatus != Status.DRAFT && attemptLockService.hasAttempts(testId)) {
            throw new ResourceConflictException("Cannot edit a question of a published test that has existing attempts.");
        }

        if (question.getSection().getTest().getSeries().getCategory() != null) {
            java.util.Set<String> providedLangs = request.getTranslations().stream()
                    .map(com.example.testservice.dto.QuestionTranslationDto::getLanguage).collect(Collectors.toSet());
            LanguageValidationUtil.validateTranslationsSet(providedLangs, question.getSection().getTest().getSeries().getCategory().getRequiredLanguages());
        }

        validateQuestionRequest(request);

        question.setQuestionType(request.getQuestionType());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setPositiveMarks(request.getPositiveMarks());
        question.setNegativeMarks(request.getNegativeMarks() != null ? request.getNegativeMarks() : BigDecimal.ZERO);
        question.setDifficulty(request.getDifficulty());

        question.getTranslations().clear();
        List<com.example.testservice.entity.QuestionTranslation> translations = request.getTranslations().stream().map(t -> 
                com.example.testservice.entity.QuestionTranslation.builder()
                        .id(UUID.randomUUID().toString())
                        .question(question)
                        .language(t.getLanguage())
                        .questionText(t.getQuestionText())
                        .optionsJson(t.getOptionsJson())
                        .build()
        ).collect(Collectors.toList());
        question.getTranslations().addAll(translations);

        return mapToPublicDto(questionRepository.save(question), null);
    }

    public List<QuestionPublicDto> getPublicQuestionsByTest(String testId, String lang) {
        return questionRepository.findByTestIdAndDeletedAtIsNull(testId).stream()
                .map(q -> mapToPublicDto(q, lang))
                .collect(Collectors.toList());
    }

    public List<QuestionInternalDto> getInternalQuestionsByTest(String testId) {
        return questionRepository.findByTestIdAndDeletedAtIsNull(testId).stream()
                .map(this::mapToInternalDto)
                .collect(Collectors.toList());
    }

    private void validateQuestionRequest(QuestionCreateRequest request) {
        if (request.getPositiveMarks().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Positive marks must be greater than 0");
        }
        if (request.getNegativeMarks() != null && request.getNegativeMarks().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Negative marks cannot be less than 0");
        }

        for (com.example.testservice.dto.QuestionTranslationDto t : request.getTranslations()) {
            MathValidationUtil.validateBalancedMathDelimiters(t.getQuestionText());

            if (request.getQuestionType() == QuestionType.NUMERICAL) {
                if (t.getOptionsJson() != null) {
                    throw new ValidationException("optionsJson must be null for NUMERICAL questions");
                }
            } else {
                if (t.getOptionsJson() == null || t.getOptionsJson().isBlank()) {
                    throw new ValidationException("optionsJson must not be null for MCQ/MULTI_CORRECT questions");
                }
                try {
                    Map<String, Object> options = objectMapper.readValue(t.getOptionsJson(), new TypeReference<>() {});
                    if (options.size() < 2) {
                        throw new ValidationException("MCQ/MULTI_CORRECT questions must have at least 2 options");
                    }
                    for (Object optionValue : options.values()) {
                        if (optionValue instanceof String) {
                            MathValidationUtil.validateBalancedMathDelimiters((String) optionValue);
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new ValidationException("Invalid optionsJson format for lang: " + t.getLanguage());
                }
            }
        }
    }

    private QuestionPublicDto mapToPublicDto(Question question, String requestedLang) {
        java.util.List<String> required = question.getSection().getTest().getSeries().getCategory() != null ? 
                question.getSection().getTest().getSeries().getCategory().getRequiredLanguages() : java.util.Collections.emptyList();
        String lang = LanguageValidationUtil.resolveLanguage(requestedLang, required);
        
        com.example.testservice.entity.QuestionTranslation translation = question.getTranslations().stream()
                .filter(t -> t.getLanguage().equalsIgnoreCase(lang))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found for language: " + lang));

        Map<String, Object> options = null;
        if (translation.getOptionsJson() != null) {
            try {
                options = objectMapper.readValue(translation.getOptionsJson(), new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                // Ignore or log
            }
        }
        
        return QuestionPublicDto.builder()
                .id(question.getId())
                .questionType(question.getQuestionType())
                .questionText(translation.getQuestionText())
                .options(options)
                .positiveMarks(question.getPositiveMarks())
                .negativeMarks(question.getNegativeMarks())
                .difficulty(question.getDifficulty())
                .build();
    }

    private QuestionInternalDto mapToInternalDto(Question question) {
        // Internal DTO currently assumes we might just need the correct answer. 
        // We can pick EN or just return the JSON string as is. 
        // Since we dropped questionText and optionsJson from Question, we should adapt the internal DTO 
        // or just return the first translation for grading if grading doesn't need text anyway.
        // Grading only needs correctAnswer. We'll put empty/null for text.
        return QuestionInternalDto.builder()
                .id(question.getId())
                .sectionId(question.getSection().getId())
                .questionType(question.getQuestionType())
                .questionText(null)
                .optionsJson(null)
                .correctAnswer(question.getCorrectAnswer())
                .positiveMarks(question.getPositiveMarks())
                .negativeMarks(question.getNegativeMarks())
                .difficulty(question.getDifficulty())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}
