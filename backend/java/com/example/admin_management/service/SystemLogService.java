package com.example.admin_management.service;


import com.example.admin_management.model.SystemLog;
import com.example.admin_management.repository.SystemLogRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;


@Service
public class SystemLogService {
    private final SystemLogRepository logRepository;


    public SystemLogService(SystemLogRepository logRepository) {
        this.logRepository = logRepository;
    }


    public void logAction(Long adminId, String action) {
        SystemLog log = new SystemLog();
        log.setAdminId(adminId);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
    }


    public List<SystemLog> getAllLogs() {
        return logRepository.findAll();
    }

    // Delete a specific log
    public void deleteLog(Long id) {
        logRepository.deleteById(id);
    }

    // Clear all logs
    public void clearAllLogs() {
        logRepository.deleteAll();
    }
}
