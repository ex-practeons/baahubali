package com.example.testservice.repository;

import com.example.testservice.entity.SectionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SectionQuestionRepository extends JpaRepository<SectionQuestion, UUID> {
}