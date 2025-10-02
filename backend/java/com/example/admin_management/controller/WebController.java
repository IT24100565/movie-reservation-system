package com.example.admin_management.controller;

import com.example.admin_management.dto.LoginRequest;
import com.example.admin_management.dto.LoginResponse;
import com.example.admin_management.service.AuthService;
import com.example.admin_management.service.MovieService;
import com.example.admin_management.service.ShowtimeService;
import com.example.admin_management.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class WebController {

    private final AuthService authService;
    private final MovieService movieService;
    private final ShowtimeService showtimeService;
    private final UserService userService;

    public WebController(AuthService authService, MovieService movieService, 
                        ShowtimeService showtimeService, UserService userService) {
        this.authService = authService;
        this.movieService = movieService;
        this.showtimeService = showtimeService;
        this.userService = userService;
    }

    /* ---------- Public pages ---------- */

    @GetMapping("/")
    public String home() {
        return "redirect:/login";  // Redirect to login since no home page needed
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request) {
        model.addAttribute("loginRequest", new LoginRequest());
        
        // Check for session expiration message
        String sessionExpired = request.getParameter("sessionExpired");
        if ("true".equals(sessionExpired)) {
            model.addAttribute("warning", "Your session has expired. Please login again.");
        }
        
        return "login";          // templates/login.html
    }

    @PostMapping("/login")
    public String doLogin(@ModelAttribute LoginRequest loginRequest,
                          Model model,
                          HttpSession session,
                          HttpServletRequest request) {

        LoginResponse response = authService.login(loginRequest);
        if (response == null) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        // Invalidate any existing session and create a new one for security
        if (session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                // Session was already invalid, ignore
            }
        }
        
        // Get new session with enhanced security
        session = request.getSession(true);
        
        // Store authentication info in session with timestamps
        long currentTime = System.currentTimeMillis();
        session.setAttribute("userId", response.getId());
        session.setAttribute("username", response.getUsername());
        session.setAttribute("role", response.getRole());
        session.setAttribute("loginTime", currentTime);
        session.setAttribute("lastActivityTime", currentTime);
        
        // Set session timeout (30 minutes) - this ensures session expires on server
        session.setMaxInactiveInterval(1800); // 30 minutes in seconds
        
        // Additional security: mark session as newly authenticated
        session.setAttribute("authenticated", true);
        session.setAttribute("sessionCreationTime", currentTime);

        // redirect based on role
        if ("ADMIN".equalsIgnoreCase(response.getRole())) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/user/dashboard";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                // Session was already invalid, ignore
            }
        }
        return "redirect:/login";
    }

    /* ---------- User pages ---------- */

    @GetMapping("/user/dashboard")
    public String userDashboard(HttpSession session) {
        if (!isUserAuthenticated(session)) {
            return "redirect:/login";
        }
        return "user/dashboard"; // templates/user/dashboard.html
    }

    /* ---------- Admin pages ---------- */

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/login";
        }
        
        // Get statistics for the dashboard using efficient count methods
        long movieCount = movieService.getMovieCount();
        long showtimeCount = showtimeService.getShowtimeCount();
        long userCount = userService.getUserCount();
        
        // Add statistics to the model
        model.addAttribute("movieCount", movieCount);
        model.addAttribute("showtimeCount", showtimeCount);
        model.addAttribute("userCount", userCount);
        
        return "admin/dashboard"; // templates/admin/dashboard.html
    }

    @GetMapping("/admin/movies")
    public String moviesPage(HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/login";
        }
        return "admin/movies";    // templates/admin/movies.html
    }

    @GetMapping("/admin/showtimes")
    public String showtimesPage(HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/login";
        }
        return "admin/showtimes"; // templates/admin/showtimes.html
    }

    @GetMapping("/admin/admins")
    public String adminsPage(HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/login";
        }
        return "admin/admins";    // templates/admin/admins.html
    }

    @GetMapping("/admin/users")
    public String usersPage(HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/login";
        }
        return "admin/users";     // templates/admin/users.html
    }

        @GetMapping("/admin/logs")
        public String logsPage(HttpSession session) {
            if (!isAdminAuthenticated(session)) {
                return "redirect:/login";
            }
            return "admin/logs";      // templates/admin/logs.html
        }
        
    /* ---------- Helper methods ---------- */
    
    private boolean isAdminAuthenticated(HttpSession session) {
        // Check if session exists and is valid
        if (session == null) {
            return false;
        }
        
        try {
            // Check if session is still valid (this will throw if session is invalid)
            session.getAttribute("username");
        } catch (IllegalStateException e) {
            // Session has been invalidated
            return false;
        }
        
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        Long userId = (Long) session.getAttribute("userId");
        
        // Check if all required session attributes are present and valid
        boolean isAuthenticated = role != null && "ADMIN".equalsIgnoreCase(role) && 
                                 username != null && !username.trim().isEmpty() &&
                                 userId != null;
        
        // If authentication fails, clean up the session
        if (!isAuthenticated && session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                // Session was already invalid, ignore
            }
        }
        
        return isAuthenticated;
    }
    
    private boolean isUserAuthenticated(HttpSession session) {
        // Check if session exists and is valid
        if (session == null) {
            return false;
        }
        
        try {
            // Check if session is still valid
            session.getAttribute("username");
        } catch (IllegalStateException e) {
            // Session has been invalidated
            return false;
        }
        
        String username = (String) session.getAttribute("username");
        Long userId = (Long) session.getAttribute("userId");
        
        boolean isAuthenticated = username != null && !username.trim().isEmpty() && userId != null;
        
        // If authentication fails, clean up the session
        if (!isAuthenticated && session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                // Session was already invalid, ignore
            }
        }
        
        return isAuthenticated;
    }
    }
