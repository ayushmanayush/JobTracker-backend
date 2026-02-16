package com.ayush.jobtracker.service;

import org.springframework.stereotype.Service;

import com.ayush.jobtracker.entity.Application;
import com.ayush.jobtracker.dto.ApplicationRequestDto;
import com.ayush.jobtracker.dto.ApplicationresponseDto;
import com.ayush.jobtracker.mapping.ApplicationCreationMapper;
import com.ayush.jobtracker.repository.ApplicationRepository;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationCreationMapper applicationmapper;
    public ApplicationService(ApplicationRepository applicationRepository,ApplicationCreationMapper applicationmapper){
        this.applicationRepository = applicationRepository;
        this.applicationmapper = applicationmapper;
    }
    public ApplicationresponseDto createApplication(ApplicationRequestDto dto){
        Application entity = applicationRepository.save(applicationmapper.toEntity(dto));
        ApplicationresponseDto tosend = applicationmapper.toUser(entity);
        return tosend;
    }
    // public List<ApplicationresponseDto> getAllApplication(){

    // }

}
