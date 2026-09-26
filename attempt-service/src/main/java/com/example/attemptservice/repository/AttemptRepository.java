package com.example.attemptservice.repository;

import com.example.attemptservice.entity.Attempt;
import com.example.attemptservice.entity.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, String> {

    List<Attempt> findByUserIdOrderByStartedAtDesc(String userId);
    
    List<Attempt> findByStatus(AttemptStatus status);
}