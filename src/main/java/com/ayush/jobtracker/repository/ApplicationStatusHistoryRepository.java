package com.ayush.jobtracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayush.jobtracker.entity.ApplicationStatusHistory;

public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory,Long>{
    
}
