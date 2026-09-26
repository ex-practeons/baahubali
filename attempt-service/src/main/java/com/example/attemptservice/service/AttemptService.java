package com.example.attemptservice.service;

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
import com.example.attemptservice.entity.Attempt;
import com.example.attemptservice.entity.AttemptStatus;
import com.example.attemptservice.event.AttemptSubmittedEvent;
import com.example.attemptservice.exception.AttemptNotFoundException;
import com.example.attemptservice.redis.AttemptRedisRepository;
import com.example.attemptservice.repository.AttemptRepository;
import com.example.attemptservice.worker.AttemptFlushWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AttemptService {

    private static final int DEFAULT_DURATION_MINUTES = 180;

    private final AttemptRepository attemptRepository;
    private final AttemptRedisRepository attemptRedisRepository;
    private final AttemptFlushWorker attemptFlushWorker;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AttemptService(AttemptRepository attemptRepository,
                          AttemptRedisRepository attemptRedisRepository,
                          @Lazy AttemptFlushWorker attemptFlushWorker,
                          KafkaTemplate<String, Object> kafkaTemplate) {
        this.attemptRepository = attemptRepository;
        this.attemptRedisRepository = attemptRedisRepository;
        this.attemptFlushWorker = attemptFlushWorker;
        this.kafkaTemplate = kafkaTemplate;
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

        return StartAttemptResponse.builder()
                .attemptId(saved.getId())
                .deadline(deadline)
                .testPayload(mockedTestPayload(saved.getTestId()))
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
        SseEmitter emitter = new SseEmitter(0L); // no timeout for now
        try {
            emitter.send(SseEmitter.event().name("connected").data(Map.of("attemptId", attemptId)));
        } catch (Exception ex) {
            emitter.completeWithError(ex);
        }
        return emitter;
    }

    public AttemptHistoryResponse getMockedHistory(String userId) {
        AttemptHistorySummary mocked = AttemptHistorySummary.builder()
                .attemptId(UUID.randomUUID().toString())
                .testId("test-456")
                .status(AttemptStatus.SUBMITTED.name())
                .finalScore(82.5)
                .startedAt(Instant.now().minusSeconds(86_400))
                .build();

        return AttemptHistoryResponse.builder()
                .attempts(List.of(mocked))
                .build();
    }

    public AttemptReviewResponse getMockedReview(String attemptId) {
        QuestionReviewDto mockedQuestion = QuestionReviewDto.builder()
                .questionId("q1")
                .questionText("Mocked question text pending test-service integration")
                .selectedOption("B")
                .correctOption("A")
                .explanation("Mocked explanation pending test-service integration")
                .build();

        return AttemptReviewResponse.builder()
                .attemptId(attemptId)
                .finalScore(82.5)
                .questions(List.of(mockedQuestion))
                .build();
    }

    private SubmitAttemptResponse toSubmitResponse(Attempt attempt) {
        return SubmitAttemptResponse.builder()
                .attemptId(attempt.getId())
                .status(attempt.getStatus().name())
                .finalScore(attempt.getFinalScore())
                .build();
    }

    private Object mockedTestPayload(String testId) {
        return Map.of(
                "testId", testId,
                "title", "Mocked Test Blueprint",
                "note", "Real blueprint fetch from test-service not wired up yet"
        );
    }
}