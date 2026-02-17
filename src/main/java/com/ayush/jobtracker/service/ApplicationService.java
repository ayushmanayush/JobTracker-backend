package com.ayush.jobtracker.service;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayush.jobtracker.entity.Application;
import com.ayush.jobtracker.entity.ApplicationStatus;
import com.ayush.jobtracker.entity.ApplicationStatusHistory;
import com.ayush.jobtracker.exception.ApplicationNotFoundException;
import com.ayush.jobtracker.exception.InvalidTransitionException;
import com.ayush.jobtracker.dto.ApplicationRequestDto;
import com.ayush.jobtracker.dto.ApplicationStatusRequestDto;
import com.ayush.jobtracker.dto.ApplicationresponseDto;
import com.ayush.jobtracker.mapping.ApplicationCreationMapper;
import com.ayush.jobtracker.repository.ApplicationRepository;
import com.ayush.jobtracker.repository.ApplicationStatusHistoryRepository;

@Service
@Transactional
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationCreationMapper applicationmapper;
    private final ApplicationStatusHistoryRepository applicationstatushistoryrepo;
    public ApplicationService(ApplicationRepository applicationRepository,ApplicationCreationMapper applicationmapper,
        ApplicationStatusHistoryRepository applicationstatushistoryrepo
    ){
        this.applicationRepository = applicationRepository;
        this.applicationmapper = applicationmapper;
        this.applicationstatushistoryrepo = applicationstatushistoryrepo;
    }
    public ApplicationresponseDto createApplication(ApplicationRequestDto dto){
        Application entity = applicationRepository.save(applicationmapper.toEntity(dto));
        ApplicationresponseDto tosend = applicationmapper.toUser(entity);
        return tosend;
    }
    public ApplicationresponseDto updateApplicationStatus(Long id,ApplicationStatusRequestDto dto){
        Application oldStatusApplication = applicationRepository.findById(id).orElseThrow(()-> new ApplicationNotFoundException("Application woth "+id+" not found"));
        ApplicationStatus oldApplicationStatus = oldStatusApplication.getStatus();//getting the old status
        boolean isValid = oldApplicationStatus.canTransitionTo(dto.getStatus());//checking valid transition
        if(!isValid){//if not valid 
            throw new InvalidTransitionException("Cannot transition from " + oldApplicationStatus + " to " + dto.getStatus());
        }
        ApplicationStatusHistory oldstatushistory= new ApplicationStatusHistory();
        oldstatushistory.setApplication(oldStatusApplication);
        oldstatushistory.setOldStatus(oldStatusApplication.getStatus());
        oldstatushistory.setNewStatus(dto.getStatus());
        oldstatushistory.setReason(dto.getReason());
        applicationstatushistoryrepo.save(oldstatushistory);
        oldStatusApplication.setStatus(dto.getStatus());
        Application newStatusUpdatedApplication = applicationRepository.save(oldStatusApplication);
        ApplicationresponseDto updatedStatusToSend = applicationmapper.toUser(newStatusUpdatedApplication);
        return updatedStatusToSend;
    }
}
