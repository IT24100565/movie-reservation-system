package com.example.admin_management.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeRequest {
    
    @NotNull(message = "Movie ID is required")
    private Long movieId;
    
    @Min(value = 1, message = "Hall ID must be valid")
    private int hallId;
    
    @NotBlank(message = "Date is required")
    private String date;
    
    @NotBlank(message = "Time is required")
    private String time;

}