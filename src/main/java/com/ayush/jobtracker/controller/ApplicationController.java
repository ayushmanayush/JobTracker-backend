package com.ayush.jobtracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayush.jobtracker.dto.ApplicationRequestDto;
import com.ayush.jobtracker.dto.ApplicationresponseDto;
import com.ayush.jobtracker.service.ApplicationService;

import jakarta.validation.Valid;

// import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
// @Validated
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;
    public ApplicationController(ApplicationService applicationService){
        this.applicationService = applicationService;
    }
    @PostMapping
    public ResponseEntity<ApplicationresponseDto> createApplication(@RequestBody @Valid ApplicationRequestDto dto){
        ApplicationresponseDto toSend = applicationService.createApplication(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toSend);
    }
    // @GetMapping
    // public ResponseEntity<List<ApplicationresponseDto>> getAllApplication(){
    //     List<ApplicationresponseDto> listToSend = applicationService.getAllApplication();
    //     return ResponseEntity.status(HttpStatus.OK).body(listToSend);
    // }
    
}
