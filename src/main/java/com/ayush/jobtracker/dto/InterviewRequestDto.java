package com.ayush.jobtracker.dto;

import java.time.LocalDateTime;

import com.ayush.jobtracker.entity.InterviewMode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InterviewRequestDto {
     @NotNull
    private Long applicationId;

    @NotNull
    @Positive
    private Integer round;

    @NotNull
    private LocalDateTime scheduledAt;

    @NotNull
    private InterviewMode mode;

    private String location;
}
