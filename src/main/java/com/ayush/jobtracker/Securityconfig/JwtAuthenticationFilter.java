package com.ayush.jobtracker.Securityconfig;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ayush.jobtracker.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
                System.out.println("working");
        //get authorization header from request
        String authHeader = request.getHeader("Authorization");

        //if header is missing or does not start with Bearer then skip JWT logic
        // we do not block request here. just pass it forward.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // extract token by removing "Bearer "
        String token = authHeader.substring(7);
        try{
        // extract username from token
        String username = jwtService.extractUsername(token);

        // important check:
        //we only set authentication if:
        // username exists
        // securityContext does NOT already have authentication
        if (username != null &&
            SecurityContextHolder.getContext().getAuthentication() == null) {

            // load user details from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // validate token against username
            if (jwtService.isTokenValid(token, userDetails.getUsername())) {

                // create authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // attach request details (IP, session info, etc.)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // set authentication in SecurityContext
                // This marks user as authenticated for this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }
    catch(Exception ex){
        //token invalid stop the filter chain here...
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return;
    }

        //continue filter chain
        filterChain.doFilter(request, response);
    }
}