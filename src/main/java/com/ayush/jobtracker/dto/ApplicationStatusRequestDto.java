package com.ayush.jobtracker.dto;

import com.ayush.jobtracker.entity.ApplicationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationStatusRequestDto {
    @NotNull
    private ApplicationStatus status;
    private String reason;
}
