package com.ayush.jobtracker.Securityconfig;



import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
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

import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    public OAuth2SuccessHandler(JwtService jwtService, UserRepository userRepository, RefreshTokenService refreshTokenService, EmailService emailService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;

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
                    user.setPassword(password);
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
        if(refreshtoken != null){
            boolean isLocal = (request.getHeader("Origin") != null && request.getHeader("Origin").contains("localhost")) ||
                              (request.getHeader("Referer") != null && request.getHeader("Referer").contains("localhost"));
            
            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshtoken)
                    .httpOnly(true)
                    .secure(!isLocal)
                    .sameSite(isLocal ? "Lax" : "None")
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        response.setContentType("application/json");
        
        String targetUrl = request.getHeader("Origin");
        if (targetUrl == null || targetUrl.isEmpty()) {
            targetUrl = request.getHeader("Referer");
        }
        
        if (targetUrl == null || targetUrl.isEmpty() || (!targetUrl.contains("localhost") && !targetUrl.contains("127.0.0.1"))) {
            targetUrl = "https://jobtracker-frontend-rccb.vercel.app";
        } else {
            if (targetUrl.endsWith("/")) {
                targetUrl = targetUrl.substring(0, targetUrl.length() - 1);
            }
        }
        
        String redirectUrl = targetUrl + "/oauthsuccess?token=" + jwt + "&name=" + name;
        response.sendRedirect(redirectUrl);
        clearAuthenticationAttributes(request);
    }
}