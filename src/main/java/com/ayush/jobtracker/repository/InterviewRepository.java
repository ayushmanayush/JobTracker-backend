package com.ayush.jobtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayush.jobtracker.entity.Interview;

public interface InterviewRepository extends JpaRepository<Interview,Long>{
    List<Interview> findByApplicationIdOrderByRoundAsc(Long id);
    boolean existsByApplicationIdAndRound(Long applicationId, int round);
    boolean existsByApplicationIdAndCompletedAtIsNull(Long applicationId);
}
