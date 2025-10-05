package com.example.ticketbookingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    public Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    public Booking booking;

    public BigDecimal amount;
    public String method; // CARD, CASH, UPI
    public String status; // INITIATED, SUCCESS, FAILED, REFUNDED

    @Column(name = "timestamp")
    public LocalDateTime timestamp;

    public Payment() {}
}