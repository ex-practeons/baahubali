package com.example.attemptservice.service;

import com.example.attemptservice.client.TestServiceFeignClient;
import com.example.attemptservice.dto.AttemptHistoryResponse;
import com.example.attemptservice.dto.AttemptHistorySummary;
import com.example.attemptservice.dto.AttemptReviewResponse;
import com.example.attemptservice.dto.AttemptStateResponse;
import com.example.attemptservice.dto.PatchAttemptRequest;
import com.example.attemptservice.dto.PatchAttemptResponse;
import com.example.attemptservice.dto.QuestionReviewDto;
import com.example.attemptservice.dto.StartAttemptRequest;
import com.example.attemptservice.dto.StartAttemptResponse;
import com.example.attemptservice.dto.SubmitAttemptResponse;
import com.example.attemptservice.dto.internal.InternalTestBlueprintDto;
import com.example.attemptservice.entity.Attempt;
import com.example.attemptservice.entity.AttemptAnswer;
import com.example.attemptservice.entity.AttemptStatus;
import com.example.attemptservice.event.AttemptSubmittedEvent;
import com.example.attemptservice.exception.AttemptNotFoundException;
import com.example.attemptservice.redis.AttemptRedisRepository;
import com.example.attemptservice.repository.AttemptAnswerRepository;
import com.example.attemptservice.repository.AttemptRepository;
import com.example.attemptservice.worker.AttemptFlushWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttemptService {

    private static final int DEFAULT_DURATION_MINUTES = 180;

    private final AttemptRepository attemptRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final AttemptRedisRepository attemptRedisRepository;
    private final AttemptFlushWorker attemptFlushWorker;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TestServiceFeignClient testServiceFeignClient;

    public AttemptService(AttemptRepository attemptRepository,
                          AttemptAnswerRepository attemptAnswerRepository,
                          AttemptRedisRepository attemptRedisRepository,
                          @Lazy AttemptFlushWorker attemptFlushWorker,
                          KafkaTemplate<String, Object> kafkaTemplate,
                          TestServiceFeignClient testServiceFeignClient) {
        this.attemptRepository = attemptRepository;
        this.attemptAnswerRepository = attemptAnswerRepository;
        this.attemptRedisRepository = attemptRedisRepository;
        this.attemptFlushWorker = attemptFlushWorker;
        this.kafkaTemplate = kafkaTemplate;
        this.testServiceFeignClient = testServiceFeignClient;
    }

    @Transactional
    public StartAttemptResponse startAttempt(StartAttemptRequest request) {
        Attempt attempt = Attempt.builder()
                .id(UUID.randomUUID().toString())
                .userId(request.getUserId())
                .testId(request.getTestId())
                .startedAt(Instant.now())
                .durationMinutes(request.getDurationMinutes() != null
                        ? request.getDurationMinutes()
                        : DEFAULT_DURATION_MINUTES)
                .status(AttemptStatus.IN_PROGRESS)
                .build();

        Attempt saved = attemptRepository.save(attempt);
        Instant deadline = saved.getStartedAt().plusSeconds(saved.getDurationMinutes() * 60L);

        // Hydrate live test data securely from test-service
        InternalTestBlueprintDto testBlueprint = testServiceFeignClient.getTestBlueprint(saved.getTestId());

        return StartAttemptResponse.builder()
                .attemptId(saved.getId())
                .deadline(deadline)
                .testPayload(testBlueprint)
                .build();
    }

    @Transactional
    public SubmitAttemptResponse submitAttempt(String attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new AttemptNotFoundException(attemptId));

        if (attempt.getStatus() == AttemptStatus.SUBMITTED || attempt.getStatus() == AttemptStatus.EXPIRED) {
            return toSubmitResponse(attempt);
        }

        finalizeAttempt(attempt, AttemptStatus.SUBMITTED);
        return toSubmitResponse(attempt);
    }

    @Transactional
    public void finalizeAttempt(Attempt attempt, AttemptStatus finalStatus) {
        if (attempt.getStatus() == AttemptStatus.SUBMITTED || attempt.getStatus() == AttemptStatus.EXPIRED) {
            return;
        }

        final String attemptId = attempt.getId();
        attempt.setStatus(finalStatus);
        attemptRepository.save(attempt);

        attemptRedisRepository.findById(attemptId).ifPresent(hash -> {
            try {
                attemptFlushWorker.flushAnswers(hash);
            } catch (Exception e) {
                log.error("Failed to flush final answers for attemptId: {}", attemptId, e);
            }
            attemptRedisRepository.deleteById(attemptId);
        });

        kafkaTemplate.send("attempt-submitted-events", attemptId, new AttemptSubmittedEvent(attemptId));
    }

    @Transactional(readOnly = true)
    public AttemptHistoryResponse getHistory(String userId) {
        List<Attempt> rawAttempts = attemptRepository.findByUserIdOrderByStartedAtDesc(userId);
        
        List<AttemptHistorySummary> historySummaries = rawAttempts.stream()
                .map(attempt -> AttemptHistorySummary.builder()
                        .attemptId(attempt.getId())
                        .testId(attempt.getTestId())
                        .status(attempt.getStatus().name())
                        .finalScore(attempt.getFinalScore())
                        .startedAt(attempt.getStartedAt())
                        .build())
                .collect(Collectors.toList());

        return AttemptHistoryResponse.builder()
                .attempts(historySummaries)
                .build();
    }

    @Transactional(readOnly = true)
    public AttemptReviewResponse getReview(String attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new AttemptNotFoundException(attemptId));

        List<AttemptAnswer> studentAnswers = attemptAnswerRepository.findByAttemptId(attemptId);
        Map<String, String> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(AttemptAnswer::getQuestionId, AttemptAnswer::getSelectedOption));

        // Fetch securely from test service
        InternalTestBlueprintDto blueprint = testServiceFeignClient.getTestBlueprint(attempt.getTestId());

        List<QuestionReviewDto> reviewDtos = new ArrayList<>();
        
        if (blueprint.getSections() != null) {
            for (InternalTestBlueprintDto.InternalSectionDto section : blueprint.getSections()) {
                if (section.getQuestions() != null) {
                    for (InternalTestBlueprintDto.InternalQuestionDto q : section.getQuestions()) {
                        
                        // Handle case where student left it completely blank
                        String selected = answerMap.getOrDefault(q.getId(), null);
                        
                        String correctOpt = null;
                        if (q.getCorrectAnswerJson() != null) {
                            if ("MCQ".equalsIgnoreCase(q.getQuestionType())) {
                                correctOpt = (String) q.getCorrectAnswerJson().get("key");
                            } else if ("MULTI_CORRECT".equalsIgnoreCase(q.getQuestionType())) {
                                Object keys = q.getCorrectAnswerJson().get("keys");
                                correctOpt = keys != null ? keys.toString() : null;
                            } else if ("NUMERICAL".equalsIgnoreCase(q.getQuestionType())) {
                                Object val = q.getCorrectAnswerJson().get("value");
                                correctOpt = val != null ? val.toString() : null;
                            }
                        }

                        String qText = (q.getTranslations() != null && !q.getTranslations().isEmpty()) 
                                ? q.getTranslations().get(0).getQuestionText() 
                                : "Question text unavailable";

                        reviewDtos.add(QuestionReviewDto.builder()
                                .questionId(q.getId())
                                .questionText(qText)
                                .selectedOption(selected)
                                .correctOption(correctOpt)
                                .explanation(q.getExplanation())
                                .build());
                    }
                }
            }
        }

        return AttemptReviewResponse.builder()
                .attemptId(attempt.getId())
                .finalScore(attempt.getFinalScore())
                .questions(reviewDtos)
                .build();
    }

    public AttemptStateResponse getMockedAttemptState(String attemptId) {
        return AttemptStateResponse.builder()
                .attemptId(attemptId)
                .userId("user-123")
                .testId("test-456")
                .status(AttemptStatus.IN_PROGRESS.name())
                .currentQuestionIndex(0)
                .answers(Map.of())
                .version(0L)
                .build();
    }

    public PatchAttemptResponse getMockedPatchAck(String attemptId, PatchAttemptRequest request) {
        long nextVersion = request.getVersion() != null ? request.getVersion() + 1 : 1L;
        return PatchAttemptResponse.builder()
                .success(true)
                .version(nextVersion)
                .build();
    }

    public SseEmitter getMockedSseEmitter(String attemptId) {
        SseEmitter emitter = new SseEmitter(0L); 
        try {
            emitter.send(SseEmitter.event().name("connected").data(Map.of("attemptId", attemptId)));
        } catch (Exception ex) {
            emitter.completeWithError(ex);
        }
        return emitter;
    }

    private SubmitAttemptResponse toSubmitResponse(Attempt attempt) {
        return SubmitAttemptResponse.builder()
                .attemptId(attempt.getId())
                .status(attempt.getStatus().name())
                .finalScore(attempt.getFinalScore())
                .build();
    }
}