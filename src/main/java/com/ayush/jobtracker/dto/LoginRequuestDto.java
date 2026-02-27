package com.ayush.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequuestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
