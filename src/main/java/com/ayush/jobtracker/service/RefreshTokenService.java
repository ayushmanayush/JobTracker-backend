package com.ayush.jobtracker.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.ayush.jobtracker.dto.RedisSessiondto;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RefreshTokenService{
    private final StringRedisTemplate redis;
    private final ObjectMapper objectmapper;
    RefreshTokenService(StringRedisTemplate redis,ObjectMapper objectmapper){
        this.redis = redis;
        this.objectmapper = objectmapper;
    }
    public String generateNewToken(String email,String ip,String deviceInfo){
        try{
        RedisSessiondto dtoforredis = new RedisSessiondto();
        dtoforredis.setDeviceInfo(deviceInfo);
        dtoforredis.setEmail(email);
        dtoforredis.setDeviceIp(ip);
        dtoforredis.setCreatedAt(LocalDateTime.now());
        String newRefreshToken = UUID.randomUUID().toString();
        String json = objectmapper.writeValueAsString(dtoforredis);
        redis.opsForValue().set("refresh:"+newRefreshToken,json,Duration.ofHours(12));
        return newRefreshToken;
        }
        catch(Exception ex){
            throw new RuntimeException("Error creating RefreshToken");
        }
    }
    public String validateRefreshToken(String token,String deviceInfo){
        if(token == null){
            return null;
        }
        String json = redis.opsForValue().get("refresh:"+token);
        if(json == null){
            deleteToken(token);
            return null;
        }
        RedisSessiondto dto = new RedisSessiondto();
        try{
         dto = objectmapper.readValue(json, RedisSessiondto.class);
        }
        catch(Exception ex) {
            throw new RuntimeException("error in fetching value from string to dto class");
        }
        if(!dto.getDeviceInfo().equals(deviceInfo)){
            deleteToken(token);
            return null;
        }
        return dto.getEmail();
    }
    public void deleteToken(String token){
        redis.delete("refresh:"+token);
    }

}
