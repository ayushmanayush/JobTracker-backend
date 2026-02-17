package com.ayush.jobtracker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "interview")
@Data
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private InterviewMode mode;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id",nullable = false)
    private Application application;
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
    @Column(nullable = false)
    private int round;
    private String location;
    private LocalDateTime createdAt;
    @PrePersist
    void setCreatedAt(){
        createdAt= LocalDateTime.now();
    }
}
