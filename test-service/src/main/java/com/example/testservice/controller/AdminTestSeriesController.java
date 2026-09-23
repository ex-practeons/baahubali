package com.example.testservice.controller;

import com.example.testservice.dto.TestSeriesDto;
import com.example.testservice.service.TestSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.testservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin/test-series")
@RequiredArgsConstructor
public class AdminTestSeriesController {
    private final TestSeriesService testSeriesService;

    private void checkAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TestSeriesDto>> createTestSeries(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                          @RequestBody TestSeriesDto request) {
        checkAdmin(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), testSeriesService.createTestSeries(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestSeriesDto>> updateTestSeries(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                          @PathVariable String id, 
                                          @RequestBody TestSeriesDto request) {
        checkAdmin(role);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), testSeriesService.updateTestSeries(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTestSeries(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                 @PathVariable String id) {
        checkAdmin(role);
        testSeriesService.deleteTestSeries(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Deleted successfully"));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<TestSeriesDto>> publishTestSeries(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                           @PathVariable String id) {
        checkAdmin(role);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), testSeriesService.publish(id)));
    }

    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<ApiResponse<TestSeriesDto>> unpublishTestSeries(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                             @PathVariable String id) {
        checkAdmin(role);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), testSeriesService.unpublish(id)));
    }
}
