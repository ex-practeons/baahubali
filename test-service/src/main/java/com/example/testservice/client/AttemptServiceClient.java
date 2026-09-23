package com.example.testservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

// Connects to the Attempt Service internally through the Gateway or Service Registry
@FeignClient(name = "attempt-service", url = "${internal.attempt-service.url:http://attempt-service:8080}")
public interface AttemptServiceClient {

    /**
     * Synchronous check to see if any students are currently taking this test.
     * Required by Option A to prevent admins from reverting a live test to DRAFT
     * while a student is in the middle of it.
     */
    @GetMapping("/internal/attempts/active-exists/{testId}")
    boolean hasActiveAttempts(
            @PathVariable("testId") UUID testId,
            @RequestHeader("X-Internal-Auth") String internalSecret
    );
}