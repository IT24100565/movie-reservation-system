package com.example.admin_management.service;


import com.example.admin_management.model.Movie;
import com.example.admin_management.repository.MovieRepository;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final SystemLogService logService;


    public MovieService(MovieRepository movieRepository, SystemLogService logService) {
        this.movieRepository = movieRepository;
        this.logService = logService;
    }


    public Movie createMovie(Movie movie, Long adminId) {
        Movie saved = movieRepository.save(movie);
        logService.logAction(adminId, "Created movie: " + movie.getTitle());
        return saved;
    }


    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }


    public Movie updateMovie(Long id, Movie updated, Long adminId) {
        return movieRepository.findById(id).map(movie -> {
            movie.setTitle(updated.getTitle());
            movie.setDescription(updated.getDescription());
            movie.setDuration(updated.getDuration());
            movie.setGenre(updated.getGenre());
            movie.setRating(updated.getRating());
            movie.setTrailerUrl(updated.getTrailerUrl());
            movie.setStatus(updated.getStatus());
            Movie saved = movieRepository.save(movie);
            logService.logAction(adminId, "Updated movie: " + saved.getTitle());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Movie not found"));
    }


    public void deleteMovie(Long id, Long adminId) {
        movieRepository.deleteById(id);
        logService.logAction(adminId, "Deleted movie with ID: " + id);
    }

    public long getMovieCount() {
        return movieRepository.count();
    }
}
