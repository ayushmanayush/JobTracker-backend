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
import com.ayush.jobtracker.service.JwtService;
import com.ayush.jobtracker.service.RefreshTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    public OAuth2SuccessHandler(JwtService jwtService, UserRepository userRepository, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;

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

        userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setFullName(name);
                    user.setPassword(UUID.randomUUID().toString());
                    return userRepository.save(user);
                });

        String jwt = jwtService.generateToken(email);
        String devce_Info = request.getHeader("User-Agent");
        String ip = request.getHeader("X-Forwarded-For");//for ip address
        if (ip == null) {
            ip = request.getRemoteAddr();// for ip address if not fetched using  getHeader("X-Forward-For")
        }
        String refreshtoken = refreshTokenService.generateNewToken(email,devce_Info,ip);
        Cookie refreshTokenCookie = new Cookie("refreshToken",refreshtoken);
        response.addCookie(refreshTokenCookie);
        response.setContentType("application/json");
        response.sendRedirect("https://jobtracker-frontend-rccb.vercel.app/oauthsuccess?token=" + jwt+"&name="+name);
        clearAuthenticationAttributes(request);
    }
}