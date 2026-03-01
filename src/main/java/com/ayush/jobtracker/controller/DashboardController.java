package com.ayush.jobtracker.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayush.jobtracker.dto.DashboardResponseDto;
import com.ayush.jobtracker.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardservice;
    public DashboardController(DashboardService dashboardservice){
        this.dashboardservice = dashboardservice;
    }
@GetMapping
public ResponseEntity<DashboardResponseDto> dashboardAnalysis(Authentication authentication){
    String username = authentication.getName();
    return ResponseEntity.status(HttpStatus.OK).body(dashboardservice.toUser(username));
}    
}
