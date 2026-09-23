package com.example.testservice.controller;

import com.example.testservice.dto.MockTestDto;
import com.example.testservice.dto.QuestionPublicDto;
import com.example.testservice.dto.TestSeriesDto;
import com.example.testservice.service.MockTestService;
import com.example.testservice.service.QuestionService;
import com.example.testservice.service.TestSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.testservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final TestSeriesService testSeriesService;
    private final MockTestService mockTestService;
    private final QuestionService questionService;

    @GetMapping("/test-series")
    public ResponseEntity<ApiResponse<Page<com.example.testservice.dto.catalog.TestSeriesResponseDto>>> getTestSeries(@RequestParam(required = false) String categoryId,
                                             @RequestParam(required = false) Boolean isFree,
                                             @RequestParam(required = false) String lang,
                                             Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), testSeriesService.getCatalog(categoryId, isFree, lang, pageable)));
    }

    @GetMapping("/test-series/{id}")
    public ResponseEntity<ApiResponse<com.example.testservice.dto.catalog.TestSeriesResponseDto>> getTestSeriesById(@PathVariable String id, @RequestParam(required = false) String lang) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), testSeriesService.getSeriesById(id, lang)));
    }

    @GetMapping("/test-series/{id}/mock-tests")
    public ResponseEntity<ApiResponse<List<com.example.testservice.dto.catalog.MockTestResponseDto>>> getMockTestsForSeries(@PathVariable String id, @RequestParam(required = false) String lang) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), mockTestService.getMockTestsBySeries(id, lang)));
    }

    @GetMapping("/mock-tests/{id}")
    public ResponseEntity<ApiResponse<com.example.testservice.dto.catalog.MockTestResponseDto>> getMockTestById(@PathVariable String id, @RequestParam(required = false) String lang) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), mockTestService.getMockTestById(id, lang)));
    }

    @GetMapping("/mock-tests/{id}/questions")
    public ResponseEntity<ApiResponse<List<QuestionPublicDto>>> getQuestionsForTest(@PathVariable String id, @RequestParam(required = false) String lang) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), questionService.getPublicQuestionsByTest(id, lang)));
    }
}
