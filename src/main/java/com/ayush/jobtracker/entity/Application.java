package com.ayush.jobtracker.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;


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
@Table(name = "application")
@Data
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String companyName;
    private String role;
    @Enumerated(EnumType.STRING)//this annotation helps the 
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_Id",nullable = false)
    private User user;
    //@prepersist - this annotations helps jpa to trigger the method below it before saving the value to Database
    @PrePersist
    public void onCreate(){
     this.createdAt=LocalDateTime.now();   
    }
}