package com.example.admin_management.repository;


import com.example.admin_management.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MovieRepository extends JpaRepository<Movie, Long> {
    long count();
}
