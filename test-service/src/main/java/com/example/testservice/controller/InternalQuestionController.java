package com.example.testservice.controller;

import com.example.testservice.dto.QuestionInternalDto;
import com.example.testservice.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import com.example.testservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/internal/mock-tests")
@RequiredArgsConstructor
public class InternalQuestionController {
    private final QuestionService questionService;

    @Value("${internal.auth.secret:secret123}")
    private String internalSecretConfig;

    @GetMapping("/{id}/questions/answer-key")
    public ResponseEntity<ApiResponse<List<QuestionInternalDto>>> getAnswerKey(@PathVariable String id,
                                                  @RequestHeader(value = "X-Internal-Auth", required = false) String internalSecret) {
        if (internalSecret == null || !internalSecret.equals(internalSecretConfig)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal auth secret");
        }
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), questionService.getInternalQuestionsByTest(id)));
    }
}
