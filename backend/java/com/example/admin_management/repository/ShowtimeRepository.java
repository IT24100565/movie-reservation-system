package com.example.admin_management.repository;


import com.example.admin_management.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    long count();
}
