package com.example.admin_management.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "Showtimes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showtimeId;


    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;


    @Min(value = 1, message = "Hall ID must be valid")
    private int hallId;


    @NotBlank(message = "Date is required")
    private String date;


    @NotBlank(message = "Time is required")
    private String time;
    
    // Temporary: Keep price field as nullable for backward compatibility
    // Later this will be handled by seat-specific pricing

}
