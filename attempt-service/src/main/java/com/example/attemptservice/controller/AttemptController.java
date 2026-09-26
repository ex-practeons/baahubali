package com.example.attemptservice.controller;

import com.example.attemptservice.dto.AttemptHistoryResponse;
import com.example.attemptservice.dto.AttemptReviewResponse;
import com.example.attemptservice.dto.AttemptStateResponse;
import com.example.attemptservice.dto.PatchAttemptRequest;
import com.example.attemptservice.dto.PatchAttemptResponse;
import com.example.attemptservice.dto.StartAttemptRequest;
import com.example.attemptservice.dto.StartAttemptResponse;
import com.example.attemptservice.dto.SubmitAttemptResponse;
import com.example.attemptservice.service.AttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/attempts")
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping
    public ResponseEntity<StartAttemptResponse> startAttempt(@RequestBody StartAttemptRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attemptService.startAttempt(request));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<SubmitAttemptResponse> submitAttempt(@PathVariable String id) {
        return ResponseEntity.ok(attemptService.submitAttempt(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttemptStateResponse> getAttempt(@PathVariable String id) {
        return ResponseEntity.ok(attemptService.getAttemptState(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PatchAttemptResponse> patchAttempt(@PathVariable String id,
                                                               @RequestBody PatchAttemptRequest request) {
        return ResponseEntity.ok(attemptService.patchAttempt(id, request));
    }

    @GetMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAttempt(@PathVariable String id) {
        return attemptService.getSseEmitter(id);
    }

    @GetMapping("/history")
    public ResponseEntity<AttemptHistoryResponse> getHistory(@RequestParam(required = false) String userId) {
        return ResponseEntity.ok(attemptService.getMockedHistory(userId));
    }

    @GetMapping("/{id}/review")
    public ResponseEntity<AttemptReviewResponse> getReview(@PathVariable String id) {
        return ResponseEntity.ok(attemptService.getMockedReview(id));
    }
}
