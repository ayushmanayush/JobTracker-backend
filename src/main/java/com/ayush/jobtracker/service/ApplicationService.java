package com.ayush.jobtracker.service;
import com.ayush.jobtracker.repository.InterviewRepository;
import com.ayush.jobtracker.repository.UserRepository;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayush.jobtracker.entity.Application;
import com.ayush.jobtracker.entity.ApplicationStatus;
import com.ayush.jobtracker.entity.ApplicationStatusHistory;
import com.ayush.jobtracker.entity.User;
import com.ayush.jobtracker.exception.ApplicationNotFoundException;
import com.ayush.jobtracker.exception.InvalidTransitionException;
import com.ayush.jobtracker.dto.ApplicationRequestDto;
import com.ayush.jobtracker.dto.ApplicationStatusRequestDto;
import com.ayush.jobtracker.dto.ApplicationresponseDto;
import com.ayush.jobtracker.mapping.ApplicationCreationMapper;
import com.ayush.jobtracker.repository.ApplicationRepository;
import com.ayush.jobtracker.repository.ApplicationStatusHistoryRepository;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDeniedException;

@Service
@Transactional
public class ApplicationService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationCreationMapper applicationmapper;
    private final ApplicationStatusHistoryRepository applicationstatushistoryrepo;
    private final UserRepository userrepo;
    private final StringRedisTemplate redis;

    public ApplicationService(ApplicationRepository applicationRepository,ApplicationCreationMapper applicationmapper,
        ApplicationStatusHistoryRepository applicationstatushistoryrepo, UserRepository userrepo, StringRedisTemplate redis
    , InterviewRepository interviewRepository){
        this.applicationRepository = applicationRepository;
        this.applicationmapper = applicationmapper;
        this.applicationstatushistoryrepo = applicationstatushistoryrepo;
        this.interviewRepository = interviewRepository;
        this.userrepo = userrepo;
        this.redis = redis;
    }
    public ApplicationresponseDto createApplication(ApplicationRequestDto dto,String useremail){
        User user = userrepo.findByEmail(useremail).orElseThrow(() -> new UsernameNotFoundException("User not found in user entity "));
        Application entity  = new Application();
        entity = applicationmapper.toEntity(dto);
        entity.setUser(user);
        entity = applicationRepository.save(entity);
        ApplicationresponseDto tosend = applicationmapper.toUser(entity);
        redis.delete("dashboard:"+user.getId());
        return tosend;
    }
    public ApplicationresponseDto updateApplicationStatus(Long id,ApplicationStatusRequestDto dto,String useremail){//application id
        Application oldStatusApplication = applicationRepository.findById(id).orElseThrow(()-> new ApplicationNotFoundException("Application woth "+id+" not found"));
        if(!oldStatusApplication.getUser().getEmail().equals(useremail)){
            throw new AccessDeniedException("Changing Status Not alowed to username: "+useremail);
        }
        ApplicationStatus oldApplicationStatus = oldStatusApplication.getStatus();//getting the old status
        boolean isValid = oldApplicationStatus.canTransitionTo(dto.getStatus());//checking valid transition
        if(!isValid){//if not valid 
            throw new InvalidTransitionException("Cannot transition from " + oldApplicationStatus + " to " + dto.getStatus());
        }
        if(dto.getStatus() == null) {
        throw new InvalidTransitionException("New status cannot be null");}
        if(oldStatusApplication.getStatus() == ApplicationStatus.INTERVIEW && interviewRepository.existsByApplicationIdAndCompletedAtIsNull(id))//if user in interview status and wants to move forward but he has scheduled an interview which he has not marked completed 
        //he will not be allowed to change the status of the application
            {
            throw new InvalidTransitionException("Cannot transist from INTERVIEW until Scheduled Interview is completed");
        }
        ApplicationStatusHistory oldstatushistory= new ApplicationStatusHistory();
        oldstatushistory.setApplication(oldStatusApplication);
        oldstatushistory.setOldStatus(oldStatusApplication.getStatus());
        oldstatushistory.setNewStatus(dto.getStatus());
        oldstatushistory.setReason(dto.getReason());
        applicationstatushistoryrepo.save(oldstatushistory);
        oldStatusApplication.setStatus(dto.getStatus());
        Application newStatusUpdatedApplication = applicationRepository.save(oldStatusApplication);
        redis.delete("dashboard:" + oldStatusApplication.getUser().getId());
        ApplicationresponseDto updatedStatusToSend = applicationmapper.toUser(newStatusUpdatedApplication);
        return updatedStatusToSend;
    }
    public List<Application> getAllApplication(String useremail){
        User user = userrepo.findByEmail(useremail).orElseThrow(() -> new UsernameNotFoundException("User with username not found in User entity"));
        List<Application> listOfApplications = applicationRepository.findByUserId(user.getId());
        return listOfApplications;
    }
}
