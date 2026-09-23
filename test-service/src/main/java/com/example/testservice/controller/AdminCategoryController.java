package com.example.testservice.controller;

import com.example.testservice.dto.CategoryDto;
import com.example.testservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;
import com.example.testservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
            @RequestHeader(value = "x-user-id") String adminId,
            @RequestBody CategoryDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), categoryService.createCategory(request, adminId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @RequestHeader(value = "x-user-id") String adminId,
            @PathVariable UUID id,
            @RequestBody CategoryDto request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), categoryService.updateCategory(id, request, adminId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), categoryService.getAllCategories()));
    }
}