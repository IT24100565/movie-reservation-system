package com.example.ticketbookingsystem.repo;

import com.example.ticketbookingsystem.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId")
    List<Seat> findByHallId(@Param("hallId") Long hallId);
    
    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId AND s.seatNumber IN :seatNumbers")
    List<Seat> lockSeatsByHallAndSeatNumbers(@Param("hallId") Long hallId, @Param("seatNumbers") List<String> seatNumbers);
    
    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.status = :status WHERE s.id = :id")
    int updateSeatStatus(@Param("id") Long id, @Param("status") String status);
    
    // New method for the simplified approach
    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId AND s.seatNumber IN :seatNumbers")
    List<Seat> findByHallIdAndSeatNumberIn(@Param("hallId") Long hallId, @Param("seatNumbers") List<String> seatNumbers);
}