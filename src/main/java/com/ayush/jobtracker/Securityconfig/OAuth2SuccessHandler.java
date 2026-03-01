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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(JwtService jwtService,
                                UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;

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

        response.setContentType("application/json");
        response.getWriter()
                .write("{\"token\":\"" + jwt + "\"}");

        clearAuthenticationAttributes(request);
    }
}