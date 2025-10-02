package com.example.admin_management.service;


import com.example.admin_management.model.User;
import com.example.admin_management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemLogService logService; // you already have this service


    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       SystemLogService logService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.logService = logService;
    }


    // Create user (encode password before saving)
    public User createUser(User user, Long performedByAdminId) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        User saved = userRepository.save(user);
        if (performedByAdminId != null) {
            logService.logAction(performedByAdminId, "Created user: " + saved.getName());
        }
        return saved;
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public User toggleUserStatus(Long id, Long adminId) {
        return userRepository.findById(id).map(u -> {
            if ("ACTIVE".equalsIgnoreCase(u.getStatus())) {
                u.setStatus("SUSPENDED");
                logService.logAction(adminId, "Suspended user: " + u.getEmail());
            } else {
                u.setStatus("ACTIVE");
                logService.logAction(adminId, "Activated user: " + u.getEmail());
            }
            return userRepository.save(u);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public long getUserCount() {
        return userRepository.count();
    }

    // Update user
    public User updateUser(Long id, User updatedUser, Long adminId) {
        return userRepository.findById(id).map(user -> {
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            user.setStatus(updatedUser.getStatus());
            // Only update password if provided
            if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
            }
            User saved = userRepository.save(user);
            logService.logAction(adminId, "Updated user: " + saved.getName());
            return saved;
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Delete user
    public void deleteUser(Long id, Long adminId) {
        userRepository.findById(id).ifPresent(user -> {
            logService.logAction(adminId, "Deleted user: " + user.getName());
            userRepository.deleteById(id);
        });
    }
}
