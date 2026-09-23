package com.example.testservice.controller;

import com.example.testservice.dto.ApiResponse;
import com.example.testservice.dto.admin.QuestionCreateDto;
import com.example.testservice.dto.admin.QuestionDetailDto;
import com.example.testservice.dto.admin.QuestionListDto;
import com.example.testservice.dto.admin.QuestionUpdateDto;
import com.example.testservice.dto.admin.QuestionUpdateResponseDto;
import com.example.testservice.dto.common.PaginatedResponseDto;
import com.example.testservice.service.admin.impl.AdminQuestionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/questions")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final AdminQuestionServiceImpl adminQuestionService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionDetailDto>> createQuestion(
            @RequestBody QuestionCreateDto request,
            @RequestHeader("x-user-id") String adminId) {
        
        UUID questionId = adminQuestionService.createQuestion(request, adminId);
        QuestionDetailDto response = adminQuestionService.getQuestionById(questionId);
        return ResponseEntity.status(201).body(ApiResponse.success(201, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponseDto<QuestionListDto>>> getQuestions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Boolean isLocked,
            @RequestParam(required = false) Boolean unused,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        
        var response = adminQuestionService.getQuestions(search, type, difficulty, isLocked, unused, page, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionDetailDto>> getQuestion(@PathVariable UUID id) {
        QuestionDetailDto response = adminQuestionService.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionUpdateResponseDto>> updateQuestion(
            @PathVariable UUID id,
            @RequestBody QuestionUpdateDto request,
            @RequestHeader("x-user-id") String adminId) { // FIX: Added adminId parameter
        
        QuestionUpdateResponseDto response = adminQuestionService.updateQuestion(id, request, adminId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteQuestion(@PathVariable UUID id) {
        adminQuestionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponse.success("Question soft-deleted successfully"));
    }
}
