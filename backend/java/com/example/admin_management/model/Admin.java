package com.example.admin_management.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "Admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;


    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be 3–50 characters")
    private String username;


    @NotBlank(message = "Password cannot be empty")
    private String passwordHash; // will be hashed before save


    @Email(message = "Email must be valid")
    private String email;


    @Pattern(regexp = "SUPER_ADMIN|STAFF", message = "Role must be SUPER_ADMIN or STAFF")
    private String role;


    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
    private String status;
}
