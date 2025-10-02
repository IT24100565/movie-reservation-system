package com.example.admin_management.controller;


import com.example.admin_management.model.User;
import com.example.admin_management.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    // Create user (POST /api/users) — send plain password in "passwordHash" field (backend will hash it)
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user, HttpSession session) {
        Long performedByAdminId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(userService.createUser(user, performedByAdminId));
    }


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    // Toggle status (suspend / activate)
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<User> toggleStatus(@PathVariable Long id, HttpSession session) {
        Long adminId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(userService.toggleUserStatus(id, adminId));
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser, HttpSession session) {
        Long adminId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(userService.updateUser(id, updatedUser, adminId));
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpSession session) {
        Long adminId = (Long) session.getAttribute("userId");
        userService.deleteUser(id, adminId);
        return ResponseEntity.noContent().build();
    }
}
