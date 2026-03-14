package com.ayush.jobtracker.dto;

import java.time.LocalDateTime;

import com.ayush.jobtracker.entity.InterviewMode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class InterviewResponseDto {
    @NotNull
    private Long applicationId;
    @NotNull
    @Positive
    private Integer round;

    @NotNull
    private LocalDateTime scheduledAt;

    @NotNull
    private InterviewMode mode;
    @NotBlank
    private String meetingDetails;
    private LocalDateTime completedAt;
    private Long id;
    private String companyName;
}
