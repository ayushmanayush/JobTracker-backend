package com.ayush.jobtracker.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandeler {
    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<Map<String,String>> ApplicationExceptionhandeler(ApplicationNotFoundException ex){
        HashMap<String,String> map = new HashMap<>();
        map.put("message" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    } 
    @ExceptionHandler(InvalidTransitionException.class)
    public ResponseEntity<Map<String,String>>  InvalidTransitionExceptionHandeler(InvalidTransitionException ex){
        HashMap<String,String> map = new HashMap<>();
        map.put("message" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }
    @ExceptionHandler(InterviewNotFound.class)
    public ResponseEntity<HashMap<String,String>> InterviewNotfoundExceptionHandeler(InterviewNotFound ex){
        HashMap<String,String> map = new HashMap<>();
        map.put("message" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    }
    @ExceptionHandler(ScheduleException.class)
    public ResponseEntity<Map<String,String>> ScheduleExceptionHandeler(ScheduleException ex){
        HashMap<String,String> map = new HashMap<>();
        map.put("message" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String,String>> userAlreadyExists(UserAlreadyExistException ex){
        HashMap<String,String> map = new HashMap<>();
        map.put("message" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,String>> accessDeniedException(AccessDeniedException ex){
        HashMap<String,String> map = new HashMap<>();
        map.put("message" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String,String>> invalidUser(AuthenticationException ex){
        HashMap<String,String> map = new HashMap<>();
        map.put("message" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error -> {
        errors.put(error.getField(), error.getDefaultMessage());
    });

    return ResponseEntity.badRequest().body(errors);
}

}
