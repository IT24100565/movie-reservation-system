package com.example.admin_management.controller;


import com.example.admin_management.dto.AdminUpdateRequest;
import com.example.admin_management.model.Admin;
import com.example.admin_management.service.AdminService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*")
public class AdminController {
    private final AdminService adminService;


    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    // Get all admins
    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }


    // Create a new admin
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@Valid @RequestBody Admin admin, HttpSession session) {
        Long performedByAdminId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(adminService.createAdmin(admin, performedByAdminId));
    }


    // Update an existing admin
    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminUpdateRequest updateRequest, HttpSession session) {
        Long performedByAdminId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(adminService.updateAdmin(id, updateRequest, performedByAdminId));
    }


    // Delete an admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id, HttpSession session) {
        Long performedByAdminId = (Long) session.getAttribute("userId");
        adminService.deleteAdmin(id, performedByAdminId);
        return ResponseEntity.noContent().build();
    }
}
