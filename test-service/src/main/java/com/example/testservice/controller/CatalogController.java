package com.example.testservice.controller;

import com.example.testservice.dto.ApiResponse;
import com.example.testservice.dto.publiccatalog.PublicMockTestStructureDto;
import com.example.testservice.service.publiccatalog.impl.CatalogServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/catalog/mock-tests")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogServiceImpl catalogService;

    @GetMapping("/{id}/structure")
    public ResponseEntity<ApiResponse<PublicMockTestStructureDto>> getTestStructure(@PathVariable UUID id) {
        // Gateway will enforce entitlement headers if this is a paid test.
        PublicMockTestStructureDto structure = catalogService.getTestStructure(id);
        return ResponseEntity.ok(ApiResponse.success(structure));
    }
}