package com.example.testservice.repository;

import com.example.testservice.entity.TestSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TestSeriesRepository extends JpaRepository<TestSeries, UUID>, JpaSpecificationExecutor<TestSeries> {
}