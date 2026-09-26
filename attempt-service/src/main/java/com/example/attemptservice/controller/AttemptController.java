package com.example.attemptservice.controller;

import com.example.attemptservice.dto.AttemptHistoryResponse;
import com.example.attemptservice.dto.AttemptReviewResponse;
import com.example.attemptservice.dto.AttemptStateResponse;
import com.example.attemptservice.dto.ApiResponse;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StartAttemptResponse>> startAttempt(
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId, 
            @RequestBody StartAttemptRequest request) {
        
        // Ensure request body adopts the secured header ID
        if (headerUserId != null && !headerUserId.isEmpty()) {
            request.setUserId(headerUserId);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "Attempt started successfully", attemptService.startAttempt(request)));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<SubmitAttemptResponse>> submitAttempt(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(200, "Attempt submitted successfully", attemptService.submitAttempt(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttemptStateResponse>> getAttempt(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(200, "Attempt retrieved successfully", attemptService.getAttemptState(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PatchAttemptResponse>> patchAttempt(@PathVariable String id,
                                                               @RequestBody PatchAttemptRequest request) {
        return ResponseEntity.ok(ApiResponse.success(200, "Attempt updated successfully", attemptService.patchAttempt(id, request)));
    }

    @GetMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAttempt(@PathVariable String id) {
        return attemptService.getSseEmitter(id);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<AttemptHistoryResponse>> getHistory(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(200, "Attempt history retrieved successfully", attemptService.getHistory(userId)));
    }

    @GetMapping("/{id}/review")
    public ResponseEntity<ApiResponse<AttemptReviewResponse>> getReview(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(200, "Attempt review retrieved successfully", attemptService.getReview(id)));
    }
}
