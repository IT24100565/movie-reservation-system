package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.model.*;
import com.example.ticketbookingsystem.repo.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PublicController {
    private final MovieRepository movieRepo;
    private final ShowtimeRepository showRepo;
    private final SeatRepository seatRepo;

    public PublicController(MovieRepository movieRepo, ShowtimeRepository showRepo, SeatRepository seatRepo) {
        this.movieRepo = movieRepo;
        this.showRepo = showRepo;
        this.seatRepo = seatRepo;
    }

    @GetMapping("/movies")
    public List<Movie> movies() {
        return movieRepo.findAll();
    }

    @GetMapping("/showtimes")
    public List<Showtime> showtimes() {
        return showRepo.findAll();
    }

    @GetMapping("/showtimes/{id}")
    public Showtime showtime(@PathVariable Long id) {
        return showRepo.findById(id).orElseThrow();
    }

    @GetMapping("/showtimes/{id}/seats")
    public List<Seat> seats(@PathVariable Long id) {
        Showtime st = showRepo.findById(id).orElseThrow();
        return seatRepo.findByHallId(st.hall.id);
    }
}