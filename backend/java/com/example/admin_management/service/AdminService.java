package com.example.admin_management.service;


import com.example.admin_management.dto.AdminUpdateRequest;
import com.example.admin_management.model.Admin;
import com.example.admin_management.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemLogService logService;


    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, SystemLogService logService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.logService = logService;
    }


    public Admin createAdmin(Admin admin, Long performedByAdminId) {
        admin.setPasswordHash(passwordEncoder.encode(admin.getPasswordHash()));
        Admin saved = adminRepository.save(admin);
        logService.logAction(performedByAdminId, "Created admin: " + saved.getUsername());
        return saved;
    }


    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }


    public Admin updateAdmin(Long id, AdminUpdateRequest updateRequest, Long performedByAdminId) {
        return adminRepository.findById(id).map(admin -> {
            admin.setUsername(updateRequest.getUsername());
            admin.setEmail(updateRequest.getEmail());
            admin.setRole(updateRequest.getRole());
            admin.setStatus(updateRequest.getStatus());


            // Only update password if provided
            if (updateRequest.getPasswordHash() != null && !updateRequest.getPasswordHash().isBlank()) {
                admin.setPasswordHash(passwordEncoder.encode(updateRequest.getPasswordHash()));
            }


            Admin saved = adminRepository.save(admin);
            logService.logAction(performedByAdminId, "Updated admin: " + saved.getUsername());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    // Backward compatibility method for Admin entity updates (still used by other parts)
    public Admin updateAdmin(Long id, Admin updated, Long performedByAdminId) {
        return adminRepository.findById(id).map(admin -> {
            admin.setUsername(updated.getUsername());
            admin.setEmail(updated.getEmail());
            admin.setRole(updated.getRole());
            admin.setStatus(updated.getStatus());


            if (updated.getPasswordHash() != null && !updated.getPasswordHash().isBlank()) {
                admin.setPasswordHash(passwordEncoder.encode(updated.getPasswordHash()));
            }


            Admin saved = adminRepository.save(admin);
            logService.logAction(performedByAdminId, "Updated admin: " + saved.getUsername());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Admin not found"));
    }


    public void deleteAdmin(Long id, Long performedByAdminId) {
        adminRepository.deleteById(id);
        logService.logAction(performedByAdminId, "Deleted admin with ID: " + id);
    }
}
