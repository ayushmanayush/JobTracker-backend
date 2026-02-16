package com.ayush.jobtracker.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequestDto {
    @NotBlank
    private String companyName;
    @NotBlank
    private String role;
    @NotNull
    private LocalDate appliedDate;
}
