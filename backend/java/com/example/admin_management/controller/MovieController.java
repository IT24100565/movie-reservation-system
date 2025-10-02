package com.example.admin_management.controller;


import com.example.admin_management.model.Movie;
import com.example.admin_management.service.MovieService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {
    private final MovieService movieService;


    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }


    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }


    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie, HttpSession session) {
        Long adminId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(movieService.createMovie(movie, adminId));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @Valid @RequestBody Movie updatedMovie, HttpSession session) {
        Long adminId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(movieService.updateMovie(id, updatedMovie, adminId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id, HttpSession session) {
        Long adminId = (Long) session.getAttribute("userId");
        movieService.deleteMovie(id, adminId);
        return ResponseEntity.noContent().build();
    }
}
