package com.example.testservice.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AttemptLockService {
    
    // In a real implementation, this would be updated by a Kafka listener consuming ATTEMPT_STARTED events.
    private final ConcurrentHashMap<String, Boolean> mockTestAttempts = new ConcurrentHashMap<>();

    public void recordAttemptStarted(String testId) {
        mockTestAttempts.put(testId, true);
    }

    public boolean hasAttempts(String testId) {
        return mockTestAttempts.getOrDefault(testId, false);
    }
}
