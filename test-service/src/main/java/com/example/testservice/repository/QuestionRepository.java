package com.example.testservice.repository;

import com.example.testservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, String> {
    List<Question> findBySectionIdAndDeletedAtIsNull(String sectionId);

    @Query("SELECT q FROM Question q JOIN q.section s WHERE s.test.id = :testId AND q.deletedAt IS NULL AND s.deletedAt IS NULL")
    List<Question> findByTestIdAndDeletedAtIsNull(@Param("testId") String testId);
}
