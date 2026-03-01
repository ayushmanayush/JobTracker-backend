package com.ayush.jobtracker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayush.jobtracker.dto.InterviewRequestDto;
import com.ayush.jobtracker.dto.InterviewResponseDto;
import com.ayush.jobtracker.entity.Application;
import com.ayush.jobtracker.entity.ApplicationStatus;
import com.ayush.jobtracker.entity.Interview;
import com.ayush.jobtracker.entity.ReminderStatus;
import com.ayush.jobtracker.exception.ApplicationNotFoundException;
import com.ayush.jobtracker.exception.InterviewNotFound;
import com.ayush.jobtracker.exception.ScheduleException;
import com.ayush.jobtracker.repository.ApplicationRepository;
import com.ayush.jobtracker.repository.InterviewRepository;

@Service
@Transactional
public class InterviewService {
    private final InterviewRepository interviewrepo;
    private final ApplicationRepository applicationrepo;
    public InterviewService(InterviewRepository interviewrepo,ApplicationRepository applicationrepo){
        this.interviewrepo  = interviewrepo;
        this.applicationrepo = applicationrepo;
    }

    public  InterviewResponseDto scheduleInterview(InterviewRequestDto dto){
        Application application = applicationrepo.findById(dto.getApplicationId()).orElseThrow(()-> new ApplicationNotFoundException("Application Not present with Id :"+dto.getApplicationId()));
        if(application.getStatus() != ApplicationStatus.INTERVIEW){
            throw new ScheduleException("Interview Cannot be scheduled in status "+ application.getStatus());
        }
        if(dto.getRound() == null || dto.getRound() <= 0){
            throw new ScheduleException("Interview round cannot be less than or equal to ZERO Or Empty");
        }
        if(interviewrepo.existsByApplicationIdAndRound(dto.getApplicationId(), dto.getRound())){
            throw new ScheduleException("round "+dto.getRound()+" already exists");
        }
        if(dto.getMeetingDetails() == null||dto.getMeetingDetails().isBlank()){
            throw new ScheduleException("Meeting Detrails should not be blank Kindly provide location or meet link in case online");
        }
        if(dto.getMode() == null){
            throw new ScheduleException("Mode cannot be null");
        }
        if(dto.getScheduledAt() == null){
            throw new ScheduleException("Schedule date and time cannot be empty");
        }
        if(dto.getScheduledAt().isBefore(LocalDateTime.now())){
            throw new ScheduleException("schedule time cannot be before date");
        }
        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setMode(dto.getMode());
        interview.setMeetingDetails(dto.getMeetingDetails());
        interview.setRound(dto.getRound());
        interview.setScheduledAt(dto.getScheduledAt());
        interview.setReminderStatus(ReminderStatus.PENDING);
        Interview saved = interviewrepo.save(interview);
        InterviewResponseDto toSend =new InterviewResponseDto();
        toSend.setApplicationId(saved.getApplication().getId());
        toSend.setCompletedAt(null);
        toSend.setMeetingDetails(saved.getMeetingDetails());
        toSend.setMode(saved.getMode());
        toSend.setRound(saved.getRound());
        toSend.setScheduledAt(saved.getScheduledAt());
        return toSend;
    }
    public List<InterviewResponseDto> getInterview(Long id){
        List<Interview> interview = interviewrepo.findByApplicationIdOrderByRoundAsc(id);
        if(interview.isEmpty()){
            throw new InterviewNotFound("Interview not found with Application id :"+id);
        }
        List<InterviewResponseDto> tosend = new ArrayList<>();
        for(Interview i : interview){
            InterviewResponseDto dto = new InterviewResponseDto();
        dto.setApplicationId(i.getApplication().getId());
        dto.setMeetingDetails(i.getMeetingDetails());
        dto.setMode(i.getMode());
        dto.setCompletedAt(i.getCompletedAt());
        dto.setRound(i.getRound());
        dto.setScheduledAt(i.getScheduledAt());
        tosend.add(dto);
        }
        return tosend;
    }
    public InterviewResponseDto markComplete(Long id){
        Interview interview =interviewrepo.findById(id).orElseThrow(()-> new ScheduleException("Interview Not Scheduled yet"));
        if(interview.getCompletedAt() != null){
        throw new ScheduleException("Interview already completed");
        }
        interview.setCompletedAt(LocalDateTime.now());
        Interview saved = interviewrepo.save(interview);
        InterviewResponseDto toSend =new InterviewResponseDto();
        toSend.setApplicationId(saved.getApplication().getId());
        toSend.setCompletedAt(saved.getCompletedAt());
        toSend.setMeetingDetails(saved.getMeetingDetails());
        toSend.setMode(saved.getMode());
        toSend.setRound(saved.getRound());
        toSend.setScheduledAt(saved.getScheduledAt());
        return toSend;
    }
}