package com.example.ticketbookingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Seats", uniqueConstraints = @UniqueConstraint(columnNames = {"hall_id", "seat_number"}))
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    public Long id;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    public Hall hall;

    @Column(name = "seat_number")
    public String seatNumber;

    @Column(name = "type")
    public String type = "REGULAR";

    @Column(name = "status")
    public String status = "AVAILABLE";

    @OneToMany(mappedBy = "seat")
    @JsonIgnore
    public List<BookingSeat> bookingSeats = new ArrayList<>();

    public Seat() {}
}