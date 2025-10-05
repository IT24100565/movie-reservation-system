package com.example.ticketbookingsystem.repo;

import com.example.ticketbookingsystem.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.showtime s LEFT JOIN FETCH s.movie LEFT JOIN FETCH s.hall LEFT JOIN FETCH b.seats bs LEFT JOIN FETCH bs.seat WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);
}