package com.example.testservice.controller;

import com.example.testservice.dto.QuestionCreateRequest;
import com.example.testservice.dto.QuestionPublicDto;
import com.example.testservice.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import com.example.testservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminQuestionController {
    private final QuestionService questionService;

    private void checkAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
    }

    @PostMapping("/sections/{sectionId}/questions")
    public ResponseEntity<ApiResponse<QuestionPublicDto>> createQuestion(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                            @PathVariable String sectionId, 
                                            @Valid @RequestBody QuestionCreateRequest request) {
        checkAdmin(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), questionService.createQuestion(sectionId, request)));
    }

    @PostMapping("/sections/{sectionId}/questions/bulk")
    public ResponseEntity<ApiResponse<List<QuestionPublicDto>>> createQuestionsBulk(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                                       @PathVariable String sectionId, 
                                                       @Valid @RequestBody List<QuestionCreateRequest> requests) {
        checkAdmin(role);
        List<QuestionPublicDto> result = requests.stream()
                .map(req -> questionService.createQuestion(sectionId, req))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), result));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<QuestionPublicDto>> updateQuestion(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                            @PathVariable String id, 
                                            @Valid @RequestBody QuestionCreateRequest request) {
        checkAdmin(role);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), questionService.updateQuestion(id, request)));
    }
}
