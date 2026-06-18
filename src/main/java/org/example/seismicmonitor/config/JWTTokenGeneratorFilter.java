package org.example.seismicmonitor.config;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.seismicmonitor.config.JWTService;
import org.example.seismicmonitor.config.SecurityConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    public JWTTokenGeneratorFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String authorities = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            String jwt = Jwts.builder()
                    .issuer("SeismicMonitor")
                    .subject(auth.getName())
                    .claim("authorities", authorities)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + SecurityConstants.JWT_EXPIRATION))
                    .signWith(jwtService.getKey())
                    .compact();

            response.setHeader("Authorization", jwt);
        }
        filterChain.doFilter(request, response);
        System.out.println("Generator filter kørt, auth: " + auth);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/api/dologin");
    }
}