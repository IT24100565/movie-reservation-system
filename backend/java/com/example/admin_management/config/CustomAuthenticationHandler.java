package com.example.admin_management.config;

import com.example.admin_management.service.AuthService;
import com.example.admin_management.dto.LoginRequest;
import com.example.admin_management.dto.LoginResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    @Autowired
    private AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        
        LoginResponse loginResponse = authService.login(loginRequest);
        
        if (loginResponse != null) {
            HttpSession session = request.getSession();
            session.setAttribute("userId", loginResponse.getId());
            session.setAttribute("username", loginResponse.getUsername());
            session.setAttribute("role", loginResponse.getRole());
            
            if ("ADMIN".equalsIgnoreCase(loginResponse.getRole())) {
                response.sendRedirect("/admin/dashboard");
            } else {
                response.sendRedirect("/user/dashboard");
            }
        } else {
            response.sendRedirect("/login?error=true");
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, 
                                      AuthenticationException exception) throws IOException, ServletException {
        response.sendRedirect("/login?error=true");
    }
}