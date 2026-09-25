package com.example.testservice.repository;

import com.example.testservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID>, JpaSpecificationExecutor<Question> {

    @Modifying
    @Query("UPDATE Question q SET q.isLocked = true WHERE q.id IN :questionIds AND q.isLocked = false")
    void lockQuestions(@Param("questionIds") Set<UUID> questionIds);
}