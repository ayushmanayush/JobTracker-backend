package com.ayush.jobtracker.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ayush.jobtracker.entity.Interview;
import com.ayush.jobtracker.entity.User;
import com.ayush.jobtracker.exception.InterviewNotFound;
import com.ayush.jobtracker.repository.InterviewRepository;
import com.ayush.jobtracker.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class EmailService {

    private final InterviewRepository interviewrepo;
    private final JavaMailSender mailSender;
    private final UserRepository userrepo;
    public EmailService(JavaMailSender mailSender, InterviewRepository interviewrepo, UserRepository userrepo){
        this.mailSender = mailSender;
        this.interviewrepo = interviewrepo;
        this.userrepo = userrepo;
    }
    @Async
    @Transactional
    public void sendInterviewReminder(Long interviewId) {
        Interview interview = interviewrepo.findById(interviewId).orElseThrow(()-> new InterviewNotFound("Interview Not scheduled for id: "+interviewId));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(interview.getApplication().getUser().getEmail());
        message.setSubject("Interview Reminder");
        message.setText(
        "Hello,\n\n" +
        "This is a reminder that you have an interview scheduled.\n\n" +
        "Company: " + interview.getApplication().getCompanyName() + "\n" +
        "Role: " + interview.getApplication().getRole() + "\n" +
        "Date & Time: " + interview.getScheduledAt() + "\n" +
        "Mode: " + interview.getMode() + "\n\n" +
        "Best of luck!\n" +
        "JobTracker Team"
        );
        mailSender.send(message);
    }
    @Async
    @Transactional
    public void sendRegisterMail(String email, String password){
        User user = userrepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("jobtrackeraka.gmail.com");
        mailMessage.setSubject("Your Login Credentials");
        mailMessage.setText("Your Login Credentials Are \n" +"email : "+email +"\nPassword : "+password+"\n Thank You For Registering Job Tracker");
        mailSender.send(mailMessage);
    }
}