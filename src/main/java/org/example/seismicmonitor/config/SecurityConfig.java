package org.example.seismicmonitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JWTService jwtService;

    @Value("${user.password}")
    private String userPassword;

    @Value("${admin.password}")
    private String adminPassword;

    public SecurityConfig(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));
                    config.setAllowedMethods(List.of("*"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setExposedHeaders(List.of("Authorization"));
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/sensor-data").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/dologin").permitAll()
                        .requestMatchers("/api/alerts/active").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/alerts/*/reports/count").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/alerts/*/reports/average-intensity").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/alerts/*/reports").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/alerts/*/reports").hasRole("USER")
                        .requestMatchers("/api/alerts/**").hasRole("ADMIN")
                        .requestMatchers("/api/sensor-readings").hasRole("ADMIN")
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                // validator before Spring Securitys own filter, so JWT is validated on all requests
                .addFilterBefore(jwtTokenValidatorFilter(), BasicAuthenticationFilter.class)
                // basic auth as fallback
                .httpBasic(httpBasic -> {});
        return http.build();
    }

    @Bean
    public JWTTokenValidatorFilter jwtTokenValidatorFilter() {
        return new JWTTokenValidatorFilter(jwtService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var user = User.builder()
                .username("Jacob")
                .password(encoder.encode(userPassword))
                .roles("USER")
                .build();

        var admin = User.builder()
                .username("admin")
                .password(encoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}