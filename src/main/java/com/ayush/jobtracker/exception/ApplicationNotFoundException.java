package com.ayush.jobtracker.exception;

public class ApplicationNotFoundException extends RuntimeException{
    public ApplicationNotFoundException(String msg){
        super(msg);
    }
}
