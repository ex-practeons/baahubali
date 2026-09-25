package com.example.attemptservice.entity;

/**
 * Mirrors the lifecycle in the architecture doc:
 * start -> in-progress -> submitted
 *                     \-> expired (TTL lapse, no submit)
 */
public enum AttemptStatus {
    IN_PROGRESS,
    SUBMITTED,
    EXPIRED
}