package com.example.admin_management.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class SessionValidationFilter implements Filter {

    // 30 minutes in milliseconds
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000;
    
    // Track last activity time for enhanced timeout control
    private static final String LAST_ACTIVITY_TIME = "lastActivityTime";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        
        // Skip validation for public paths
        if (isPublicPath(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check session validity for protected admin paths
        if (requestURI.startsWith("/admin")) {
            HttpSession session = httpRequest.getSession(false);
            
            if (!isValidAdminSession(session)) {
                // Clear any invalid session
                if (session != null) {
                    try {
                        session.invalidate();
                    } catch (IllegalStateException e) {
                        // Session was already invalid
                    }
                }
                
                // For AJAX requests, return JSON error
                if (isAjaxRequest(httpRequest)) {
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpResponse.setContentType("application/json");
                    httpResponse.getWriter().write("{\"error\": \"Session expired. Please login again.\", \"redirect\": \"/login\"}");
                    return;
                }
                
                // Redirect to login for regular requests
                httpResponse.sendRedirect("/login?sessionExpired=true");
                return;
            }
            
            // Update last activity time for valid sessions
            updateLastActivityTime(session);
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean isPublicPath(String requestURI) {
        return requestURI.equals("/") || 
               requestURI.equals("/login") || 
               requestURI.equals("/logout") ||
               requestURI.startsWith("/css/") ||
               requestURI.startsWith("/js/") ||
               requestURI.startsWith("/images/") ||
               requestURI.startsWith("/static/") ||
               requestURI.equals("/api/auth/login");
    }
    
    private boolean isValidAdminSession(HttpSession session) {
        if (session == null) {
            return false;
        }
        
        try {
            // Check if session is still valid
            String role = (String) session.getAttribute("role");
            String username = (String) session.getAttribute("username");
            Long userId = (Long) session.getAttribute("userId");
            Long loginTime = (Long) session.getAttribute("loginTime");
            Long lastActivityTime = (Long) session.getAttribute(LAST_ACTIVITY_TIME);
            
            // Validate all required attributes are present
            if (role == null || !"ADMIN".equalsIgnoreCase(role) || 
                username == null || username.trim().isEmpty() || 
                userId == null || loginTime == null) {
                return false;
            }
            
            long currentTime = System.currentTimeMillis();
            
            // Check 30-minute inactivity timeout
            if (lastActivityTime != null) {
                long inactiveTime = currentTime - lastActivityTime;
                if (inactiveTime > SESSION_TIMEOUT) {
                    return false;
                }
            }
            
            // Check maximum session age (8 hours as additional security)
            long sessionAge = currentTime - loginTime;
            long maxSessionAge = 8 * 60 * 60 * 1000; // 8 hours
            
            if (sessionAge > maxSessionAge) {
                return false;
            }
            
            return true;
            
        } catch (IllegalStateException e) {
            // Session has been invalidated
            return false;
        }
    }
    
    private void updateLastActivityTime(HttpSession session) {
        if (session != null) {
            try {
                session.setAttribute(LAST_ACTIVITY_TIME, System.currentTimeMillis());
            } catch (IllegalStateException e) {
                // Session was invalidated, ignore
            }
        }
    }
    
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String contentType = request.getHeader("Content-Type");
        return "XMLHttpRequest".equals(requestedWith) || 
               (contentType != null && contentType.contains("application/json"));
    }
}