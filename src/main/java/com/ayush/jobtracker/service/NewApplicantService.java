package com.ayush.jobtracker.service;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayush.jobtracker.dto.NewApplicantRequestDto;
import com.ayush.jobtracker.entity.User;
import com.ayush.jobtracker.exception.UserAlreadyExistException;
import com.ayush.jobtracker.repository.UserRepository;


@Service
@Transactional
public class NewApplicantService {
    private final UserRepository userrepo;
    private final PasswordEncoder encoder;
    public NewApplicantService(UserRepository userrepo, PasswordEncoder encoder){
        this.userrepo = userrepo;
        this.encoder = encoder;
    }
    public void createuser(NewApplicantRequestDto dto){
        if(userrepo.existsByEmail(dto.getEmail())){
            throw new UserAlreadyExistException("User with Email Already Exists");
        }
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        try{
        userrepo.save(user);
        }
        catch(DataIntegrityViolationException ex){
            throw new UserAlreadyExistException("User with Email Already Exists");
        }
    }
}
