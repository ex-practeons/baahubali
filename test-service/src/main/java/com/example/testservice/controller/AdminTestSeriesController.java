package com.example.testservice.controller;

import com.example.testservice.dto.ApiResponse;
import com.example.testservice.dto.admin.TestSeriesCreateDto;
import com.example.testservice.dto.admin.TestSeriesDetailDto;
import com.example.testservice.dto.admin.TestSeriesListDto;
import com.example.testservice.dto.admin.TestSeriesUpdateDto;
import com.example.testservice.dto.common.PaginatedResponseDto;
import com.example.testservice.service.admin.impl.AdminTestSeriesServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/series")
@RequiredArgsConstructor
public class AdminTestSeriesController {

    private final AdminTestSeriesServiceImpl adminTestSeriesService;

    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> createSeries(
            @RequestBody TestSeriesCreateDto request,
            @RequestHeader("x-user-id") String adminId) {
            
        UUID seriesId = adminTestSeriesService.createSeries(request, adminId);
        return ResponseEntity.status(201).body(ApiResponse.success(seriesId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponseDto<TestSeriesListDto>>> getSeriesList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
            
        var response = adminTestSeriesService.getSeriesList(search, status, categoryId, page, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestSeriesDetailDto>> getSeriesById(@PathVariable UUID id) {
        TestSeriesDetailDto response = adminTestSeriesService.getSeriesById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateSeries(
            @PathVariable UUID id,
            @RequestBody TestSeriesUpdateDto request,
            @RequestHeader("x-user-id") String adminId) {
            
        adminTestSeriesService.updateSeries(id, request, adminId);
        return ResponseEntity.ok(ApiResponse.success("Test Series updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteSeries(@PathVariable UUID id) {
        adminTestSeriesService.deleteSeries(id);
        return ResponseEntity.ok(ApiResponse.success("Test Series soft-deleted successfully"));
    }
}