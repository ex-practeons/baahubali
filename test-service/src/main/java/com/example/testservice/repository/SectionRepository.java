package com.example.testservice.repository;

import com.example.testservice.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, String> {
    List<Section> findByTestIdAndDeletedAtIsNullOrderBySequenceOrderAsc(String testId);
}
