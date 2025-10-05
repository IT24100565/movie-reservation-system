package com.example.ticketbookingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Bookings")
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    public Long id;

    @Column(name = "user_id")
    public Long userId;

    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    public Showtime showtime;

    // Removed booking_date field since it doesn't exist in the database

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<BookingSeat> seats = new ArrayList<>();

    @OneToOne(mappedBy = "booking")
    @JsonIgnore
    public Payment payment; // optional until paid

    @Column(name = "status")
    public String status = "PENDING";

    @Column(name = "timestamp")
    public LocalDateTime timestamp;

    public Booking() {}
}