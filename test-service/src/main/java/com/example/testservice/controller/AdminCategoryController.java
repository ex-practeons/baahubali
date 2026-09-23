package com.example.testservice.controller;

import com.example.testservice.dto.CategoryDto;
import com.example.testservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import com.example.testservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    private void checkAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                      @RequestBody CategoryDto request) {
        checkAdmin(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), categoryService.createCategory(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@RequestHeader(value = "X-User-Role", required = false) String role, 
                                      @PathVariable String id, 
                                      @RequestBody CategoryDto request) {
        checkAdmin(role);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), categoryService.updateCategory(id, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategories(@RequestHeader(value = "X-User-Role", required = false) String role) {
        checkAdmin(role);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), categoryService.getAllCategories()));
    }
}
