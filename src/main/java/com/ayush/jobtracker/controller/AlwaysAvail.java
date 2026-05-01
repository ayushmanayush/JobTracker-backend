package com.ayush.jobtracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/on")
public class AlwaysAvail {
    @GetMapping
    public ResponseEntity<HttpStatus> oK(){
        return ResponseEntity.ok().build();
    }
}
