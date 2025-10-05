package com.example.ticketbookingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Movies")
public class Movie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    public Long id;

    public String title;
    @Column(columnDefinition = "nvarchar(max)")
    public String description;
    public Integer duration;
    public String genre;
    public String rating;
    public String trailer_url;
    public String status = "ACTIVE";

    @OneToMany(mappedBy = "movie")
    @JsonIgnore
    public List<Showtime> showtimes = new ArrayList<>();

    public Movie() {}
}