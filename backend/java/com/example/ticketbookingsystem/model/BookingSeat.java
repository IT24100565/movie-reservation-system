package com.example.ticketbookingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "BookingSeats")
public class BookingSeat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    public Booking booking;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    public Seat seat;

    public BookingSeat() {}
    public BookingSeat(Booking booking, Seat seat) {
        this.booking = booking; this.seat = seat;
    }
}