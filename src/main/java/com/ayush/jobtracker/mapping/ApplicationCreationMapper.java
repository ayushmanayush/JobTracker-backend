package com.ayush.jobtracker.mapping;

import org.springframework.stereotype.Component;

import com.ayush.jobtracker.dto.ApplicationRequestDto;
import com.ayush.jobtracker.dto.ApplicationresponseDto;
import com.ayush.jobtracker.entity.Application;
import com.ayush.jobtracker.entity.ApplicationStatus;
@Component
public class ApplicationCreationMapper {
    public Application toEntity(ApplicationRequestDto dto){
        Application app = new Application();
        app.setCompanyName(dto.getCompanyName());
        app.setRole(dto.getRole());
        app.setAppliedDate(dto.getAppliedDate());
        app.setStatus(ApplicationStatus.APPLIED);
        return app;
    }
    public ApplicationresponseDto toUser(Application app){
        ApplicationresponseDto dto = new ApplicationresponseDto();
        dto.setCompanyName(app.getCompanyName());
        dto.setAppliedDate(app.getAppliedDate());
        dto.setRole(app.getRole());
        dto.setStatus(app.getStatus());
        dto.setId(app.getId());
        return dto;
    }
}
