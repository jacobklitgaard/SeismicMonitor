package org.example.seismicmonitor.controller;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import org.example.seismicmonitor.config.JWTService;
import org.example.seismicmonitor.config.SecurityConstants;
import org.example.seismicmonitor.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public LoginController(AuthenticationManager authenticationManager, JWTService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/dologin")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        if (auth.isAuthenticated()) {
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
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }
}