package com.example.testservice.repository;

import com.example.testservice.entity.MockTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockTestRepository extends JpaRepository<MockTest, String> {
    List<MockTest> findBySeriesIdAndDeletedAtIsNull(String seriesId);
}
