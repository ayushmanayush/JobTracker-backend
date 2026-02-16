package com.ayush.jobtracker.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "application")
@Data
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String companyName;
    @NotBlank
    private String role;
    @NotNull
    @Enumerated(EnumType.STRING)//this annotation helps the 
    private ApplicationStatus status;
    @NotNull
    private LocalDate appliedDate;
    private LocalDateTime createdAt;
    private Long userId;
    //@prepersist - this annotations helps jpa to trigger the method below it before saving the value to Database
    @PrePersist
    public void onCreate(){
     this.createdAt=LocalDateTime.now();   
    }
}