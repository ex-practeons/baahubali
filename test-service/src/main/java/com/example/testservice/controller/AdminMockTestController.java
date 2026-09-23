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
    public ResponseEntity<ApiResponse<String>> publishTest(
            @PathVariable UUID id,
            @RequestHeader(value = "If-Match", required = true) Instant expectedUpdatedAt) {
        
        adminMockTestService.publishTest(id, expectedUpdatedAt);
        return ResponseEntity.ok(ApiResponse.success("Test published successfully"));
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
    public ResponseEntity<ApiResponse<String>> updateMockTest(
            @PathVariable UUID id,
            @RequestBody com.example.testservice.dto.admin.MockTestCreateDto request) {
        
        adminMockTestService.updateMockTest(id, request);
        return ResponseEntity.ok(ApiResponse.success("Test updated successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<com.example.testservice.dto.publiccatalog.PublicMockTestStructureDto>> getMockTestSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(adminMockTestService.getMockTestSummary(id)));
    }

    @GetMapping("/{id}/answer-key")
    public ResponseEntity<ApiResponse<com.example.testservice.dto.internal.TestBlueprintDto>> getMockTestAnswerKey(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(adminMockTestService.getTestWithAnswers(id))); 
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<String>> archiveTest(@PathVariable UUID id) {
        adminMockTestService.archiveTest(id);
        return ResponseEntity.ok(ApiResponse.success("Test archived successfully"));
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<ApiResponse<UUID>> cloneTest(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, String> body,
            @RequestHeader("x-user-id") String adminId) {
        
        String newTitle = body.get("newTitle");
        UUID clonedId = adminMockTestService.cloneTest(id, newTitle, adminId);
        return ResponseEntity.ok(ApiResponse.success(clonedId));
    }

    @PatchMapping("/{id}/revert-to-draft")
    public ResponseEntity<ApiResponse<String>> revertToDraft(@PathVariable UUID id) {
        adminMockTestService.revertToDraft(id);
        return ResponseEntity.ok(ApiResponse.success("Test reverted to DRAFT"));
    }
}
