package com.example.testservice.repository;

import com.example.testservice.entity.MockTest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MockTestRepository extends JpaRepository<MockTest, UUID> {
    List<MockTest> findBySeriesIdAndDeletedAtIsNull(UUID seriesId);
}