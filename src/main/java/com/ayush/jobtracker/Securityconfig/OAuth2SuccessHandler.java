package com.ayush.jobtracker.Securityconfig;



import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ayush.jobtracker.service.JwtService;
import com.ayush.jobtracker.service.OAuthUserService;
import com.ayush.jobtracker.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OAuthUserService oauthService;
    public OAuth2SuccessHandler(JwtService jwtService, RefreshTokenService refreshTokenService ,@Lazy OAuthUserService oauthService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.oauthService = oauthService;
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
        oauthService.handleUser(email, name);
        String jwt = jwtService.generateToken(email);
        String devce_Info = request.getHeader("User-Agent");
        String ip = request.getHeader("X-Forwarded-For");//for ip address
        if (ip == null) {
            ip = request.getRemoteAddr();// for ip address if not fetched using  getHeader("X-Forward-For")
        }
        String refreshtoken = refreshTokenService.generateNewToken(email,devce_Info,ip);
        String state = request.getParameter("state");
        if(refreshtoken != null){
            
            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshtoken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
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
        
        String redirectUrl = targetUrl + "/oauthsuccess?token=" + jwt+ "&name=" + URLEncoder.encode(name,StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
        clearAuthenticationAttributes(request);
    }
}