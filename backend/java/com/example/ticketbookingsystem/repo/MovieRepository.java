
package com.example.ticketbookingsystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ticketbookingsystem.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
