package com.ayush.jobtracker.Securityconfig;



import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ayush.jobtracker.entity.User;
import com.ayush.jobtracker.repository.UserRepository;
import com.ayush.jobtracker.service.EmailService;
import com.ayush.jobtracker.service.JwtService;
import com.ayush.jobtracker.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordencoder;
    public OAuth2SuccessHandler(JwtService jwtService, UserRepository userRepository, RefreshTokenService refreshTokenService, EmailService emailService,PasswordEncoder passwordencoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
        this.passwordencoder = passwordencoder;

        setRedirectStrategy((request, response, url) -> {});
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        OAuth2AuthenticationToken token =
                (OAuth2AuthenticationToken) authentication;

        Map<String, Object> attributes =
                token.getPrincipal().getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String password = UUID.randomUUID().toString();
        userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setFullName(name);
                    user.setPassword(passwordencoder.encode(password));
                    User savedUser = userRepository.save(user);
                    emailService.sendRegisterMail(email, password);
                    return savedUser;
                });

        String jwt = jwtService.generateToken(email);
        String devce_Info = request.getHeader("User-Agent");
        String ip = request.getHeader("X-Forwarded-For");//for ip address
        if (ip == null) {
            ip = request.getRemoteAddr();// for ip address if not fetched using  getHeader("X-Forward-For")
        }
        String refreshtoken = refreshTokenService.generateNewToken(email,devce_Info,ip);
        String state = request.getParameter("state");
        if(refreshtoken != null){
            String origin = request.getHeader("Origin");
            boolean isLocal = origin != null && origin.contains("localhost");
            
            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshtoken)
                    .httpOnly(true)
                    .secure(!isLocal)
                    .sameSite(isLocal ? "Lax" : "None")
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        
        String targetUrl;
        if("LOCAL".equals(state))
            {
                targetUrl = "http://localhost:5173";
            }
            else{
                targetUrl = "https://jobtracker-frontend-rccb.vercel.app";
            }
        
        String redirectUrl = targetUrl + "/oauthsuccess?token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8) + "&name=" + URLEncoder.encode(name,StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
        clearAuthenticationAttributes(request);
    }
}