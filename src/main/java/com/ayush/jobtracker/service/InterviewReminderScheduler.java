package com.ayush.jobtracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ayush.jobtracker.entity.Interview;
import com.ayush.jobtracker.entity.ReminderStatus;
import com.ayush.jobtracker.repository.InterviewRepository;

@Service
public class InterviewReminderScheduler {

    private final InterviewRepository interviewRepository;
    private final EmailService emailService;

    public InterviewReminderScheduler(InterviewRepository interviewRepository,
                                       EmailService emailService) {
        this.interviewRepository = interviewRepository;
        this.emailService = emailService;
    }

    // Runs 30 seconds AFTER previous execution completes
    @Scheduled(fixedDelay = 30000)
    public void processInterviewReminders() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.plusHours(3); // reminder 3 hours before
        LocalDateTime retryTime = now.minusMinutes(5); // retry stuck PROCESSING jobs

        // Fetch limited batch (you must add this repo method next)
        List<Interview> interviews =
                interviewRepository.findEligibleReminders(
                        targetTime,
                        retryTime,
                        PageRequest.of(0, 100)
                );

        for (Interview interview : interviews) {
            try {

                // Mark as PROCESSING
                interview.setReminderStatus(ReminderStatus.PROCESSING);
                interview.setReminderProcessedAt(LocalDateTime.now());
                interviewRepository.save(interview);

                //  Send email
                emailService.sendInterviewReminder(interview.getId());

            } catch (Exception e) {

                // If sending fails, revert back to PENDING
                interview.setReminderStatus(ReminderStatus.PENDING);
                interviewRepository.save(interview);
            }
        }
    }
}
