package com.example.testservice.controller;

import com.example.testservice.dto.ApiResponse;
import com.example.testservice.dto.admin.BulkAttachQuestionsDto;
import com.example.testservice.dto.admin.AdminSectionDetailDto;
import com.example.testservice.dto.admin.SectionCreateDto;
import com.example.testservice.service.admin.impl.AdminSectionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminSectionController {

    private final AdminSectionServiceImpl adminSectionService;

    @PostMapping("/mock-tests/{testId}/sections")
    public ResponseEntity<ApiResponse<AdminSectionDetailDto>> createSection(
            @PathVariable UUID testId,
            @RequestBody SectionCreateDto request,
            @RequestHeader("x-user-id") String adminId) {
        UUID sectionId = adminSectionService.createSection(testId, request, adminId);
        AdminSectionDetailDto response = adminSectionService.getSectionDetail(sectionId);
        return ResponseEntity.status(201).body(ApiResponse.success(201, response));
    }

    @PutMapping("/mock-tests/{testId}/sections/reorder")
    public ResponseEntity<ApiResponse<String>> reorderSections(
            @PathVariable UUID testId,
            @RequestBody Map<String, List<UUID>> request) {
        adminSectionService.reorderSections(testId, request.get("orderedSectionIds"));
        return ResponseEntity.ok(ApiResponse.success("Sections reordered successfully"));
    }

    // --- Question Mapping Operations ---

    @PostMapping("/sections/{id}/questions")
    public ResponseEntity<ApiResponse<String>> attachQuestionsToSection(
            @PathVariable UUID id,
            @RequestBody BulkAttachQuestionsDto request) {
        adminSectionService.attachQuestions(id, request);
        return ResponseEntity.ok(ApiResponse.success("Questions attached successfully"));
    }

    @DeleteMapping("/sections/{sectionId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<String>> removeQuestionFromSection(
            @PathVariable UUID sectionId,
            @PathVariable UUID questionId) {
        adminSectionService.removeQuestionFromSection(sectionId, questionId);
        return ResponseEntity.ok(ApiResponse.success("Question removed from section"));
    }

    @PutMapping("/sections/{sectionId}/questions/reorder")
    public ResponseEntity<ApiResponse<String>> reorderQuestionsInSection(
            @PathVariable UUID sectionId,
            @RequestBody Map<String, List<UUID>> request) {
        adminSectionService.reorderQuestions(sectionId, request.get("orderedQuestionIds"));
        return ResponseEntity.ok(ApiResponse.success("Questions reordered successfully"));
    }

    @PatchMapping("/sections/{sectionId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<String>> updateQuestionMarks(
            @PathVariable UUID sectionId,
            @PathVariable UUID questionId,
            @RequestBody Map<String, BigDecimal> request) {
        adminSectionService.updateMarksOverride(sectionId, questionId, request.get("positiveMarks"), request.get("negativeMarks"));
        return ResponseEntity.ok(ApiResponse.success("Question marks updated for this section"));
    }
}
