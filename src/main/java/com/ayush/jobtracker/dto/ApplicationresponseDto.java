package com.ayush.jobtracker.dto;

import java.time.LocalDate;

import com.ayush.jobtracker.entity.ApplicationStatus;

import lombok.Data;

@Data
public class ApplicationresponseDto {
    private Long id;
    private String companyName;
    private String role;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private boolean hasActiveInterview;
}
