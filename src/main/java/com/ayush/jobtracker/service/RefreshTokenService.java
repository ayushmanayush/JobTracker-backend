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
    public RefreshTokenService(StringRedisTemplate redis,ObjectMapper objectmapper){
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
            System.err.println("CRITICAL: Redis error in RefreshTokenService for user: " + email);
            ex.printStackTrace(); 
            return null;
        }
    }
    public String validateRefreshToken(String token,String deviceInfo){
        try{
        if(token == null){
            return null;
        }
        String json = redis.opsForValue().get("refresh:"+token);
        if(json == null){
            deleteToken(token);
            return null;
        }
        RedisSessiondto dto = objectmapper.readValue(json, RedisSessiondto.class);
        if(!dto.getDeviceInfo().equals(deviceInfo)){
            deleteToken(token);
            return null;
        }
        return dto.getEmail();
    }
    catch(Exception ex){
        System.out.println("redis Unavailable");
        return null;
    }
    }
    public void deleteToken(String token){
        try{
        redis.delete("refresh:"+token);
        }
        catch(Exception ex){
            System.out.println("redis Unavailable");
        }
    }

}
