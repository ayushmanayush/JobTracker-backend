package com.ayush.jobtracker.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ayush.jobtracker.dto.DashboardResponseDto;
import com.ayush.jobtracker.entity.ApplicationStatus;
import com.ayush.jobtracker.entity.User;
import com.ayush.jobtracker.repository.ApplicationRepository;
import com.ayush.jobtracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DashboardService {

    private final ApplicationRepository applicationrepo;
    private final UserRepository userrepo;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public DashboardService(ApplicationRepository applicationrepo,
            UserRepository userrepo,
            StringRedisTemplate redis,
            ObjectMapper objectMapper) {
        this.applicationrepo = applicationrepo;
        this.userrepo = userrepo;
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public DashboardResponseDto toUser(String username) {
        User user = userrepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Long userId = user.getId();
        String key = "dashboard:" + userId;
        try { // trying in redis if data available
            String cached = redis.opsForValue().get(key);
            if (cached != null) {
                return objectMapper.readValue(cached, DashboardResponseDto.class);
            }
        } catch (Exception e) {
            System.out.println("Redis error: " + e.getMessage());
        }
        long total = applicationrepo.countByUserId(userId);
        long offered = applicationrepo.countByUserIdAndStatusIn(userId, java.util.List.of(
                ApplicationStatus.OFFERED,
                ApplicationStatus.ACCEPTED,
                ApplicationStatus.DECLINED));
        long rejected = applicationrepo.countByUserIdAndStatus(userId, ApplicationStatus.REJECTED);

        DashboardResponseDto dto = new DashboardResponseDto();
        dto.setTotalApplications(total);
        dto.setTotalOffered(offered);
        dto.setTotalRejected(rejected);

        if (total != 0) {
            dto.setOfferRate((offered * 100.0) / total);
            dto.setRejectionRate((rejected * 100.0) / total);
        }

        try {
            String json = objectMapper.writeValueAsString(dto);
            redis.opsForValue().set(key, json, Duration.ofMinutes(10));
        } catch (Exception e) {
            System.out.println("Redis error: " + e.getMessage());
        }

        return dto;
    }
}
