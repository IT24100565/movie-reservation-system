package com.example.admin_management.service;


import com.example.admin_management.dto.LoginRequest;
import com.example.admin_management.dto.LoginResponse;
import com.example.admin_management.model.Admin;
import com.example.admin_management.model.User;
import com.example.admin_management.repository.AdminRepository;
import com.example.admin_management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {


    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthService(AdminRepository adminRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public LoginResponse login(LoginRequest request) {
        // Try Admins first
        var adminOpt = adminRepository.findByUsername(request.getUsername());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
                // map any admin role (SUPER_ADMIN or STAFF) to "ADMIN" for frontend convenience
                return new LoginResponse(admin.getAdminId(), admin.getUsername(), "ADMIN");
            }
        }


        // Try Users (use name as username)
        var userOpt = userRepository.findByName(request.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                return new LoginResponse(user.getUserId(), user.getName(), "USER");
            }
        }


        // Invalid credentials
        return null;
    }
}
