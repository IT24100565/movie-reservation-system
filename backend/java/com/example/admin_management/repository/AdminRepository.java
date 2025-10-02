package com.example.admin_management.repository;


import com.example.admin_management.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
}
