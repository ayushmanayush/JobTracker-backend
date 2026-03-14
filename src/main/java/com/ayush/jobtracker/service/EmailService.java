package com.ayush.jobtracker.service;

import org.springframework.beans.factory.annotation.Value;
// import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ayush.jobtracker.entity.Interview;
import com.ayush.jobtracker.exception.InterviewNotFound;
import com.ayush.jobtracker.repository.InterviewRepository;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

import jakarta.transaction.Transactional;

@Service
public class EmailService {

    private final InterviewRepository interviewrepo;
    // private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender, InterviewRepository interviewrepo){
        // this.mailSender = mailSender;
        this.interviewrepo = interviewrepo;
    }
    // @Async
    // @Transactional
    // public void sendInterviewReminder(Long interviewId) {
    //     Interview interview = interviewrepo.findById(interviewId).orElseThrow(()-> new InterviewNotFound("Interview Not scheduled for id: "+interviewId));
    //     SimpleMailMessage message = new SimpleMailMessage();
    //     message.setTo(interview.getApplication().getUser().getEmail());
    //     message.setSubject("Interview Reminder");
    //     message.setText(
    //     "Hello,\n\n" +
    //     "This is a reminder that you have an interview scheduled.\n\n" +
    //     "Company: " + interview.getApplication().getCompanyName() + "\n" +
    //     "Role: " + interview.getApplication().getRole() + "\n" +
    //     "Date & Time: " + interview.getScheduledAt() + "\n" +
    //     "Mode: " + interview.getMode() + "\n\n" +
    //     "Best of luck!\n" +
    //     "JobTracker Team"
    //     );
    //     mailSender.send(message);
    // }
    // @Async
    // @Transactional
    // public void sendRegisterMail(String email, String password){
    //     SimpleMailMessage mailMessage = new SimpleMailMessage();
    //     mailMessage.setTo(email);
    //     mailMessage.setFrom("jobtrackeraka.gmail.com");
    //     mailMessage.setSubject("Your Login Credentials");
    //     mailMessage.setText("Your Login Credentials Are \n" +"email : "+email +"\nPassword : "+password+"\n Thank You For Registering Job Tracker");
    //     mailSender.send(mailMessage);
    // }
    // render do not support SMTP(simple mail transfer protocol port and will not be able to send mail so using resend as a third party service would be nice)
    @Value("${RESEND_API_KEY}")
    String apikey;
    @Async
    @Transactional
    public void sendInterviewReminder(Long interviewId){
        Interview interview = interviewrepo.findById(interviewId).orElseThrow(() -> new InterviewNotFound("Interview does not exists with id : "+interviewId));
        String email = interview.getApplication().getUser().getEmail();
        Resend resend = new Resend(apikey);
        String html ="<h2>Interview Reminder</h2>" +"<p>Hello,</p>" +"<p>This is a reminder that you have an interview scheduled.</p>" +
                            "<p><b>Company:</b> " + interview.getApplication().getCompanyName() + "</p>" +
                            "<p><b>Role:</b> " + interview.getApplication().getRole() + "</p>" +
                            "<p><b>Date & Time:</b> " + interview.getScheduledAt() + "</p>" +
                            "<p><b>Mode:</b> " + interview.getMode() + "</p>" +"<br>" +
                            "<p>Best of luck!</p>" +
                            "<p><b>JobTracker Team</b></p>";
        CreateEmailOptions sendemail = CreateEmailOptions.builder()
                .from("Jobtracker <onboarding@resend.dev>")
                .to(email)
                .subject("Reminder!!!!")
                .html(html)
                .build();
                try{
                   resend.emails().send(sendemail);
                }
                catch(ResendException ex){
                    ex.printStackTrace();
                }
        
    }
    @Async 
    @Transactional
    public void sendRegisterMail(String email,String Password){
        Resend resend = new Resend(apikey);
        CreateEmailOptions mailsender = CreateEmailOptions.builder()
        .from("Jobtracker <onboarding@resend.dev>")
                .to(email)
                .subject("Login Credentials")
                .html("<h1>Welcome To Jobtracker</h1><br/><h2>Your Login credentials Are</h2><br><p>email : "+email+"</p><br/><p>Password : "+Password)
                .build();

                try{
                    resend.emails().send(mailsender);
                }
                catch(ResendException ex){
                    ex.printStackTrace();
                }
    }

}