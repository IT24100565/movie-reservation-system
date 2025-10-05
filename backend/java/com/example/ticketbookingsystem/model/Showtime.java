package com.example.ticketbookingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Showtimes")
public class Showtime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showtime_id")
    public Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    public Movie movie;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    public Hall hall;

    @Column(name = "date")
    public LocalDate date;

    @Column(name = "time")
    public LocalTime time;

    @OneToMany(mappedBy = "showtime")
    @JsonIgnore
    public List<Booking> bookings = new ArrayList<>();

    public java.math.BigDecimal price;

    public Showtime() {}
}