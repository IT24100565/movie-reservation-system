package com.example.admin_management.repository;


import com.example.admin_management.model.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
}
