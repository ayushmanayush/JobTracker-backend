package com.ayush.jobtracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayush.jobtracker.dto.ApplicationRequestDto;
import com.ayush.jobtracker.dto.ApplicationStatusRequestDto;
import com.ayush.jobtracker.dto.ApplicationresponseDto;
import com.ayush.jobtracker.entity.Application;
import com.ayush.jobtracker.service.ApplicationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;



// import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@Validated
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;
    public ApplicationController(ApplicationService applicationService){
        this.applicationService = applicationService;
    }
    @PostMapping
    public ResponseEntity<ApplicationresponseDto> createApplication(@RequestBody @Valid ApplicationRequestDto dto,Authentication authentication){
        String user = authentication.getName();
        ApplicationresponseDto toSend = applicationService.createApplication(dto,user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toSend);
    }
    @PatchMapping("/{id}/status") // here id means Application id
    public ResponseEntity<ApplicationresponseDto> updateApplication(@PathVariable @NotNull(message = "Id should not be null") Long id,@RequestBody @Valid ApplicationStatusRequestDto dto,Authentication authentication){
        ApplicationresponseDto dtoToSend = applicationService.updateApplicationStatus(id, dto,authentication.getName());
        return ResponseEntity.status(HttpStatus.OK).body(dtoToSend);
    }
    @GetMapping
    public ResponseEntity<List<Application>> fetchAllApplication(Authentication auth){
        String email = auth.getName();
        return ResponseEntity.status(HttpStatus.OK).body(applicationService.getAllApplication(email));
    }
}
