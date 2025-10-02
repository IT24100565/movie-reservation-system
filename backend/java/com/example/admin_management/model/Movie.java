package com.example.admin_management.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "Movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;


    @NotBlank(message = "Title cannot be empty")
    private String title;


    @Size(max = 1000, message = "Description too long")
    private String description;


    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int duration;


    @NotBlank(message = "Genre cannot be empty")
    private String genre;


    private String rating;


    @Pattern(regexp = "^(https?://.*)?$", message = "Trailer URL must be valid")
    private String trailerUrl;


    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
    private String status;
}
