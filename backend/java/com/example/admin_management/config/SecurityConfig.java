package com.example.admin_management.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {


    // Use cost 10 (matches $2a$10$ hashes)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }


    // Enhanced security configuration that works with session-based authentication
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to static resources and login pages
                        .requestMatchers("/", "/login", "/logout", "/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                        // Allow login API endpoint
                        .requestMatchers("/api/auth/login").permitAll()
                        // All other requests will be handled by our interceptors and controllers
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
