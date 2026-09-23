package com.example.testservice.controller;

import com.example.testservice.dto.MockTestDto;
import com.example.testservice.service.MockTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.testservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin/mock-tests")
@RequiredArgsConstructor
public class AdminMockTestController {
    private final MockTestService mockTestService;

    private void checkAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MockTestDto>> createMockTest(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                      @RequestBody MockTestDto request) {
        checkAdmin(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), mockTestService.createMockTest(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MockTestDto>> updateMockTest(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                      @PathVariable String id, 
                                      @RequestBody MockTestDto request) {
        checkAdmin(role);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), mockTestService.updateMockTest(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMockTest(@RequestHeader(value = "X-User-Role", required = false) String role, 
                               @PathVariable String id) {
        checkAdmin(role);
        mockTestService.deleteMockTest(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Deleted successfully"));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<MockTestDto>> publishMockTest(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                       @PathVariable String id) {
        checkAdmin(role);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), mockTestService.publish(id)));
    }
}
