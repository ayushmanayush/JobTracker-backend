package com.ayush.jobtracker.Securityconfig;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ayush.jobtracker.entity.User;

//Needed to connect user entity to spring security if this is not added spring will not recognize your DB of user and will make it's own in memory user like before 
public class CustomUserDetails implements UserDetails{
    private final User user;
    public CustomUserDetails(User user){
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // no roles for now we are not adding role based access so we are returning empty list
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // login using email so we are gonna 
    }

    @Override
    public boolean isAccountNonExpired() { // we can add in user's account that is it expired or not in user db afterwards
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {// we can add in user's account that is it locked or not or not in user db afterwards
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // we can add in user's account that is it's password has expired and have to change it afterwards
        return true;
    }

    @Override
    public boolean isEnabled() { // is account enabled we can add this parameter in user entity afterward
        return true;
    }
    //some of the criteria has not been added to keep this project simple those are returning true by default if it will be added then we will return user.get();
}
