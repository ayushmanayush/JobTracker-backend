package com.ayush.jobtracker.service;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ayush.jobtracker.entity.User;
import com.ayush.jobtracker.repository.UserRepository;

@Service
public class OAuthUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public OAuthUserService(UserRepository userRepository,
                            @Lazy PasswordEncoder passwordEncoder,
                            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void handleUser(String email, String name) {
        userRepository.findByEmail(email)
                .orElseGet(() -> {
                    String password = UUID.randomUUID().toString();

                    User user = new User();
                    user.setEmail(email);
                    user.setFullName(name);
                    user.setPassword(passwordEncoder.encode(password));
                    User saved = userRepository.save(user);
                    emailService.sendRegisterMail(email, password);
                    return saved;
                });
    }
}