package com.ayush.jobtracker.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayush.jobtracker.entity.*;



public interface ApplicationRepository extends JpaRepository<Application,Long>{
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, ApplicationStatus status);
    List<Application> findByUserId(Long userId);
}
