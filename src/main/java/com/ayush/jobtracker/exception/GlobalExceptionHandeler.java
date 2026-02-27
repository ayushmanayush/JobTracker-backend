package com.ayush.jobtracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandeler {
    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<String> ApplicationExceptionhandeler(ApplicationNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    } 
    @ExceptionHandler(InvalidTransitionException.class)
    public ResponseEntity<String>  InvalidTransitionExceptionHandeler(InvalidTransitionException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(InterviewNotFound.class)
    public ResponseEntity<String> InterviewNotfoundExceptionHandeler(InterviewNotFound ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(ScheduleException.class)
    public ResponseEntity<String> ScheduleExceptionHandeler(ScheduleException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<String> userAlreadyExists(UserAlreadyExistException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
