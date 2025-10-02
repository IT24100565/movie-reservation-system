package com.example.admin_management.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;


    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50)
    @Column(name = "name", nullable = false, unique = true)
    private String name; // acts as username


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, unique = true)
    private String email;


    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // store bcrypt hash (backend will write this)


    @NotBlank(message = "Role is required")
    @Column(name = "role", nullable = false)
    private String role; // "USER"


    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false)
    private String status; // ACTIVE / SUSPENDED
}
