package com.ayush.jobtracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayush.jobtracker.Securityconfig.CustomUserDetails;
import com.ayush.jobtracker.dto.LoginRequuestDto;
import com.ayush.jobtracker.dto.LoginResponseDto;
import com.ayush.jobtracker.dto.NewApplicantRequestDto;
import com.ayush.jobtracker.service.JwtService;
import com.ayush.jobtracker.service.NewApplicantService;
import com.ayush.jobtracker.service.RefreshTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final NewApplicantService newApplicantservice;
    private final RefreshTokenService refreshtokenservice;
    public AuthController(NewApplicantService newApplicantservice, AuthenticationManager authenticationManager,JwtService jwtService, RefreshTokenService refreshtokenservice){
        this.newApplicantservice = newApplicantservice;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshtokenservice = refreshtokenservice;

    }
    @PostMapping("/register")
    public ResponseEntity<String> CreateAccount(@RequestBody @Valid NewApplicantRequestDto dto){
        newApplicantservice.createuser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("SignUp Successfull");
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequuestDto dto,HttpServletResponse response, HttpServletRequest request){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        CustomUserDetails userDetails  =(CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails.getUsername());
        String ip = request.getHeader("X-Forwarded-For");//for ip address
        if (ip == null) {
            ip = request.getRemoteAddr();// for ip address if not fetched using  getHeader("X-Forward-For")
        }
        String deviceInfo = request.getHeader("User-Agent");// device information 
        String newrefreshtoken = refreshtokenservice.generateNewToken(userDetails.getUsername(),ip,deviceInfo);
        Cookie cookie = new Cookie("refreshToken",newrefreshtoken);
        cookie.setSecure(true);//false in local or else will not run
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDto(token));
    }
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookie = request.getCookies();
        if(cookie == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String refreshToken = null;
        for(Cookie cok: cookie){
            if("refreshToken".equals(cok.getName())){
                refreshToken = cok.getValue();
                break;
            }
        }
        if(refreshToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String deviceInfo = request.getHeader("User-Agent");
        String email = refreshtokenservice.validateRefreshToken(refreshToken,deviceInfo);
        if (email == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
        String ip = request.getHeader("X-Forwarded-For");
        if(ip == null){
            ip = request.getRemoteAddr();
        }
        refreshtokenservice.deleteToken(refreshToken);
        String newRefreshToken = refreshtokenservice.generateNewToken(email,ip,deviceInfo);
        Cookie newCookie = new Cookie("refreshToken", newRefreshToken);
        newCookie.setHttpOnly(true);
        newCookie.setSecure(true);//false in local or else will not work 
        newCookie.setPath("/");
        newCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(newCookie);
        String newaccesstoken = jwtService.generateToken(email);
        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDto(newaccesstoken));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cokkie = request.getCookies();
        if(cokkie == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String refreshtoken = null;
        for(Cookie cook : cokkie){
            if("refreshToken".equals(cook.getName())){
                refreshtoken = cook.getValue();
            }
        }
        if(refreshtoken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String deviceInfo = request.getHeader("User-Agent");
        String email = refreshtokenservice.validateRefreshToken(refreshtoken, deviceInfo);
        if(email == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        refreshtokenservice.deleteToken(refreshtoken);
        Cookie clearCookie = new Cookie("refreshToken", null);
        clearCookie.setHttpOnly(true);
        clearCookie.setSecure(true); // false in local
        clearCookie.setPath("/");
        clearCookie.setMaxAge(0);
        response.addCookie(clearCookie);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("User Logged out Successfully");
    }
}