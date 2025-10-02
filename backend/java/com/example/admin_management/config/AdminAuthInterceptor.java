package com.example.admin_management.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    
    // 30 minutes in milliseconds
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000;
    private static final String LAST_ACTIVITY_TIME = "lastActivityTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // Allow login-related endpoints
        if (requestURI.equals("/api/auth/login") || requestURI.equals("/login") || requestURI.equals("/")) {
            return true;
        }
        
        // Check admin API endpoints
        if (requestURI.startsWith("/api/") && 
            (requestURI.startsWith("/api/admins") || 
             requestURI.startsWith("/api/movies") || 
             requestURI.startsWith("/api/showtimes") || 
             requestURI.startsWith("/api/users") || 
             requestURI.startsWith("/api/systemlogs"))) {
            
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Authentication required\", \"redirect\": \"/login\"}");
                response.setContentType("application/json");
                return false;
            }
            
            // Validate session and check timeout
            if (!isValidAdminSession(session)) {
                // Invalidate expired session
                try {
                    session.invalidate();
                } catch (IllegalStateException e) {
                    // Session was already invalid
                }
                
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Session expired. Please login again.\", \"redirect\": \"/login\"}");
                response.setContentType("application/json");
                return false;
            }
            
            // Update last activity time for valid sessions
            updateLastActivityTime(session);
        }
        
        return true;
    }
    
    private boolean isValidAdminSession(HttpSession session) {
        try {
            String role = (String) session.getAttribute("role");
            String username = (String) session.getAttribute("username");
            Long userId = (Long) session.getAttribute("userId");
            Long lastActivityTime = (Long) session.getAttribute(LAST_ACTIVITY_TIME);
            
            // Check if all required session attributes are present
            if (role == null || !"ADMIN".equalsIgnoreCase(role) || username == null || userId == null) {
                return false;
            }
            
            // Check 30-minute inactivity timeout
            if (lastActivityTime != null) {
                long currentTime = System.currentTimeMillis();
                long inactiveTime = currentTime - lastActivityTime;
                if (inactiveTime > SESSION_TIMEOUT) {
                    return false;
                }
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
}