package com.example.testservice.repository;

import com.example.testservice.entity.TestSeries;
import com.example.testservice.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TestSeriesRepository extends JpaRepository<TestSeries, String> {
    
    @Query("SELECT ts FROM TestSeries ts WHERE " +
           "ts.deletedAt IS NULL AND ts.status = :status AND " +
           "(:categoryId IS NULL OR ts.category.id = :categoryId) AND " +
           "(:isFree IS NULL OR ts.isFree = :isFree)")
    Page<TestSeries> findAllByFilters(@Param("status") Status status,
                                      @Param("categoryId") String categoryId,
                                      @Param("isFree") Boolean isFree,
                                      Pageable pageable);
}
