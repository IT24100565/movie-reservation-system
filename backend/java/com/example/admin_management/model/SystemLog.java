package com.example.admin_management.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "SystemLogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;


    private Long adminId; // who did the action
    private String action; // description of what happened
    private LocalDateTime timestamp;
}
