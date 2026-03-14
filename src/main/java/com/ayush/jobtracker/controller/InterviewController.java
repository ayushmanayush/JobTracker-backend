package com.ayush.jobtracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayush.jobtracker.dto.InterviewRequestDto;
import com.ayush.jobtracker.dto.InterviewResponseDto;
import com.ayush.jobtracker.service.InterviewService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/interviews")
public class InterviewController {
    private final InterviewService interviewservice;
    public InterviewController(InterviewService interviewservice){
        this.interviewservice = interviewservice;
    }
    @PostMapping
    public ResponseEntity<InterviewResponseDto> postInterview(@RequestBody @Valid InterviewRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewservice.scheduleInterview(dto));
    }
    @GetMapping("/application/{id}")
    public ResponseEntity<List<InterviewResponseDto>> getLatestInterview(@PathVariable @NotNull(message = "application id should not be null") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(interviewservice.getInterview(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<InterviewResponseDto> competeInterview(@PathVariable @NotNull(message = "Id cannot be null") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(interviewservice.markComplete(id));
    }
    @GetMapping("/open")
    public ResponseEntity<List<InterviewResponseDto>> getOpenInterviews(java.security.Principal principal){
        return ResponseEntity.status(HttpStatus.OK).body(interviewservice.getOpenInterviews(principal.getName()));
    }
}
