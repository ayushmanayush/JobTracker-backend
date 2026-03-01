package com.ayush.jobtracker.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RedisSessiondto {
    private String email;
    private String deviceInfo;
    private String deviceIp;
    private LocalDateTime createdAt;
}
