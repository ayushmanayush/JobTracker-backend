package com.ayush.jobtracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayush.jobtracker.dto.DashboardResponseDto;
import com.ayush.jobtracker.service.DashboardService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardservice;
    public DashboardController(DashboardService dashboardservice){
        this.dashboardservice = dashboardservice;
    }
@GetMapping("/{id}")
public ResponseEntity<DashboardResponseDto> dashboardAnalysis(@PathVariable @NotNull(message = "UserId should not be null") Long id){
    return ResponseEntity.status(HttpStatus.OK).body(dashboardservice.toUser(id));
}    
}
