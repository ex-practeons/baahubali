package com.example.attemptservice.repository;

import com.example.attemptservice.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, String> {

    List<Attempt> findByUserIdOrderByStartedAtDesc(String userId);
}