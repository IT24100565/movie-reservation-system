package com.example.admin_management.controller;


import com.example.admin_management.model.SystemLog;
import com.example.admin_management.service.SystemLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class SystemLogController {
    private final SystemLogService logService;


    public SystemLogController(SystemLogService logService) {
        this.logService = logService;
    }


    @GetMapping
    public List<SystemLog> getAllLogs() {
        return logService.getAllLogs();
    }

    // Delete a specific log
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        logService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    // Clear all logs
    @DeleteMapping
    public ResponseEntity<Void> clearAllLogs() {
        logService.clearAllLogs();
        return ResponseEntity.noContent().build();
    }
}
