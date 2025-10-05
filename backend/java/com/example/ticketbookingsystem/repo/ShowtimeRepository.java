package com.example.ticketbookingsystem.repo;

import com.example.ticketbookingsystem.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {}
