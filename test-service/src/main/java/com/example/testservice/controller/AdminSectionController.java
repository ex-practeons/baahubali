package com.example.testservice.controller;

import com.example.testservice.dto.SectionDto;
import com.example.testservice.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.testservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin/mock-tests/{testId}/sections")
@RequiredArgsConstructor
public class AdminSectionController {
    private final SectionService sectionService;

    private void checkAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SectionDto>> createSection(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                    @PathVariable String testId, 
                                    @RequestBody SectionDto request) {
        checkAdmin(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), sectionService.createSection(testId, request)));
    }
}
