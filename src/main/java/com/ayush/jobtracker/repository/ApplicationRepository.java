package com.ayush.jobtracker.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ayush.jobtracker.entity.*;



public interface ApplicationRepository extends JpaRepository<Application,Long>{
    long countByUserId(Long id);
    long countByUserIdAndStatus(Long id,ApplicationStatus status);
}
