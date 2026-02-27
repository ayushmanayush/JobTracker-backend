package com.ayush.jobtracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayush.jobtracker.Securityconfig.CustomUserDetails;
import com.ayush.jobtracker.dto.LoginRequuestDto;
import com.ayush.jobtracker.dto.LoginResponseDto;
import com.ayush.jobtracker.dto.NewApplicantRequestDto;
import com.ayush.jobtracker.service.JwtService;
import com.ayush.jobtracker.service.NewApplicantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final NewApplicantService newApplicantservice;
    public AuthController(NewApplicantService newApplicantservice, AuthenticationManager authenticationManager,JwtService jwtService){
        this.newApplicantservice = newApplicantservice;
                this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    @PostMapping("/register")
    public ResponseEntity<String> CreateAccount(@RequestBody @Valid NewApplicantRequestDto dto){
        newApplicantservice.createuser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("SignUp Successfull");
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequuestDto dto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        CustomUserDetails userDetails  =(CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDto(token));
    }
}