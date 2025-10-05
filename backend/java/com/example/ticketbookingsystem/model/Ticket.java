package com.example.ticketbookingsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Tickets")
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    public Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    public Booking booking;

    @Column(name = "qr_code")
    public String qrCode;

    @Column(name = "issue_date")
    public LocalDateTime issueDate;

    public Ticket() {}
}


