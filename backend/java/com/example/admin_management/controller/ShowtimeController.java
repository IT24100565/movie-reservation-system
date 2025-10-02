package com.example.admin_management.controller;


import com.example.admin_management.dto.ShowtimeRequest;
import com.example.admin_management.model.Movie;
import com.example.admin_management.model.Showtime;
import com.example.admin_management.service.MovieService;
import com.example.admin_management.service.ShowtimeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "*")
public class ShowtimeController {
    private final ShowtimeService showtimeService;
    private final MovieService movieService;


    public ShowtimeController(ShowtimeService showtimeService, MovieService movieService) {
        this.showtimeService = showtimeService;
        this.movieService = movieService;
    }


    @GetMapping
    public List<Showtime> getAllShowtimes() {
        return showtimeService.getAllShowtimes();
    }


    @PostMapping
    public ResponseEntity<Showtime> createShowtime(@Valid @RequestBody ShowtimeRequest request, HttpSession session) {
        // Convert DTO to entity
        Movie movie = movieService.getMovieById(request.getMovieId());
        if (movie == null) {
            throw new RuntimeException("Movie not found with ID: " + request.getMovieId());
        }
        
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setHallId(request.getHallId());
        showtime.setDate(request.getDate());
        showtime.setTime(request.getTime());
        
        Long adminId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(showtimeService.createShowtime(showtime, adminId));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Showtime> updateShowtime(@PathVariable Long id, @Valid @RequestBody ShowtimeRequest request, HttpSession session) {
        // Convert DTO to entity
        Movie movie = movieService.getMovieById(request.getMovieId());
        if (movie == null) {
            throw new RuntimeException("Movie not found with ID: " + request.getMovieId());
        }
        
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setMovie(movie);
        updatedShowtime.setHallId(request.getHallId());
        updatedShowtime.setDate(request.getDate());
        updatedShowtime.setTime(request.getTime());
        
        Long adminId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(showtimeService.updateShowtime(id, updatedShowtime, adminId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id, HttpSession session) {
        Long adminId = (Long) session.getAttribute("userId");
        showtimeService.deleteShowtime(id, adminId);
        return ResponseEntity.noContent().build();
    }
}
