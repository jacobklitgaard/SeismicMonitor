package org.example.seismicmonitor.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    public JWTTokenValidatorFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    // Validates the JWT from the request header and sets the authenticated user in the Spring Security context.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");
        if (jwt != null) {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(jwtService.getKey())
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();

                var auth = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(), null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(
                                (String) claims.get("authorities")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid token");
            }
        }
        // sends to next filter
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/api/dologin");
    }
}

