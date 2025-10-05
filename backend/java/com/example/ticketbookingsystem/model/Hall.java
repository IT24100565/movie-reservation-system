package com.example.ticketbookingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Halls")
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hall_id")
    public Long id;

    @Column(name = "name")
    public String name;

    @OneToMany(mappedBy = "hall")
    @JsonIgnore
    public List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "hall")
    @JsonIgnore
    public List<Showtime> showtimes = new ArrayList<>();

    public Hall() {}
}