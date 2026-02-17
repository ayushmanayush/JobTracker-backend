package com.ayush.jobtracker.dto;
import lombok.Data;

@Data
public class DashboardResponseDto {
    private long totalApplications;
    private long totalAccepted;
    private long totalRejected;
    private double offerRate;
    private double rejectionRate;
}
