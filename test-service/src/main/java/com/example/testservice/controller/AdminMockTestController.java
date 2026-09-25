package com.example.testservice.controller;

import com.example.testservice.dto.ApiResponse;
import com.example.testservice.dto.admin.AdminMockTestDetailDto;
import com.example.testservice.service.admin.impl.AdminMockTestServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/admin/mock-tests")
@RequiredArgsConstructor
public class AdminMockTestController {

    private final AdminMockTestServiceImpl adminMockTestService;

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<AdminMockTestDetailDto>> publishTest(
            @PathVariable UUID id,
            @RequestHeader(value = "If-Match", required = true) Instant expectedUpdatedAt) {
        
        AdminMockTestDetailDto response = adminMockTestService.publishTest(id, expectedUpdatedAt);
        return ResponseEntity.ok(ApiResponse.success(200, "Test published successfully", response));
    }

    @PostMapping("/series/{seriesId}/mock-tests")
    public ResponseEntity<ApiResponse<AdminMockTestDetailDto>> createMockTest(
            @PathVariable UUID seriesId,
            @RequestBody com.example.testservice.dto.admin.MockTestCreateDto request,
            @RequestHeader("x-user-id") String adminId) {
        
        UUID testId = adminMockTestService.createMockTest(seriesId, request, adminId);
        AdminMockTestDetailDto response = adminMockTestService.getAdminMockTestDetail(testId);
        return ResponseEntity.status(201).body(ApiResponse.success(201, response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminMockTestDetailDto>> updateMockTest(
            @PathVariable UUID id,
            @RequestBody com.example.testservice.dto.admin.MockTestCreateDto request) {
        
        AdminMockTestDetailDto response = adminMockTestService.updateMockTest(id, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Test updated successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminMockTestDetailDto>> getMockTestSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(adminMockTestService.getAdminMockTestDetail(id)));
    }

    @GetMapping("/{id}/answer-key")
    public ResponseEntity<ApiResponse<com.example.testservice.dto.internal.TestBlueprintDto>> getMockTestAnswerKey(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(adminMockTestService.getTestWithAnswers(id))); 
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<AdminMockTestDetailDto>> archiveTest(@PathVariable UUID id) {
        AdminMockTestDetailDto response = adminMockTestService.archiveTest(id);
        return ResponseEntity.ok(ApiResponse.success(200, "Test archived successfully", response));
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<ApiResponse<AdminMockTestDetailDto>> cloneTest(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, String> body,
            @RequestHeader("x-user-id") String adminId) {
        
        String newTitle = body.get("newTitle");
        AdminMockTestDetailDto response = adminMockTestService.cloneTest(id, newTitle, adminId);
        return ResponseEntity.ok(ApiResponse.success(200, "Test cloned successfully", response));
    }

    @PatchMapping("/{id}/revert-to-draft")
    public ResponseEntity<ApiResponse<AdminMockTestDetailDto>> revertToDraft(@PathVariable UUID id) {
        AdminMockTestDetailDto response = adminMockTestService.revertToDraft(id);
        return ResponseEntity.ok(ApiResponse.success(200, "Test reverted to DRAFT", response));
    }
}
