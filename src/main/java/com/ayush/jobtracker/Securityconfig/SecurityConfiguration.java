package com.ayush.jobtracker.Securityconfig;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,OAuth2SuccessHandler oAuth2SuccessHandler) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();}
    @Bean
    public PasswordEncoder encodePassword(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5173","http://localhost:5173","https://jobtracker-frontend-rccb.vercel.app/"));//frontend for localhost we as we are creating frontend in react
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception{
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/register").permitAll()
            .requestMatchers("/oauth2/**").permitAll()
            .requestMatchers("/auth/login").permitAll()
            .requestMatchers("/auth/logout").authenticated()
            .requestMatchers("/applications/**").authenticated()
            .requestMatchers("/interviews/**").authenticated()
            .requestMatchers("/dashboard/**").authenticated()
            .anyRequest().authenticated()
        );
        http.oauth2Login(oauth -> oauth.successHandler(oAuth2SuccessHandler));
        http.addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement(Session -> Session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();

    }
}