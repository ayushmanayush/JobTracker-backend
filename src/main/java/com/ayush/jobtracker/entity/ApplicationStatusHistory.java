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
@Table(name = "applicationstatushistory")
@Data
public class ApplicationStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)//By default Many to one relation type is eager so we specify lazy explicitly
    @JoinColumn(name = "application_id",nullable = false)
    private Application application;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus oldStatus;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus newStatus;
    private LocalDateTime changedAt;
    private Long changedBy;
    @Column(nullable = true)
    private String reason;
    @PrePersist
    public void setDateTime(){
        changedAt = LocalDateTime.now();
    }
}
