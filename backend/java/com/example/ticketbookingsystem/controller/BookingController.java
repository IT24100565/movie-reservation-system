package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.service.BookingService;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    public BookingController(BookingService bookingService) { this.bookingService = bookingService; }

    public static record CreateRequest(Long showtimeId, List<String> seats, Long userId) {}
    public static record CreateResponse(String bookingCode, Long bookingId) {}

    @PostMapping
    public CreateResponse create(@RequestBody CreateRequest req) {
        var res = bookingService.createBooking(req.showtimeId(), req.seats(), req.userId());
        return new CreateResponse(res.bookingCode(), res.bookingId());
    }

    public static record UpdateRequest(Long showtimeId) {}
    public static record UpdateResponse(String bookingCode, Long bookingId) {}

    @PutMapping("/{bookingId}")
    public UpdateResponse update(@PathVariable Long bookingId, @RequestBody UpdateRequest req) {
        var res = bookingService.updateBooking(bookingId, req.showtimeId());
        return new UpdateResponse(res.bookingCode(), res.bookingId());
    }

    public static record UpdateSeatsRequest(Long showtimeId, List<String> seats) {}
    
    @PutMapping("/{bookingId}/seats")
    public UpdateResponse updateSeats(@PathVariable Long bookingId, @RequestBody UpdateSeatsRequest req) {
        System.out.println("Update seats request for booking: " + bookingId);
        System.out.println("Showtime ID: " + req.showtimeId() + ", Seats: " + req.seats());
        
        // Use the new simplified method
        var res = bookingService.simpleUpdateBooking(bookingId, req.showtimeId(), req.seats());
        return new UpdateResponse(res.bookingCode(), res.bookingId());
    }

    public static record UpdateShowtimeRequest(Long showtimeId) {}
    
    @PutMapping("/{bookingId}/showtime")
    public UpdateResponse updateShowtime(@PathVariable Long bookingId, @RequestBody UpdateShowtimeRequest req) {
        System.out.println("Update showtime request for booking: " + bookingId + " to showtime: " + req.showtimeId());
        
        // Use the new very simple method
        var res = bookingService.verySimpleUpdateBookingShowtime(bookingId, req.showtimeId());
        return new UpdateResponse(res.bookingCode(), res.bookingId());
    }

    @DeleteMapping("/{bookingId}")
    public void cancel(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    public static record BookingDto(Long id, String status, String movieTitle, String date, String time, java.util.List<String> seats, java.math.BigDecimal total) {}

    @GetMapping
    public java.util.List<BookingDto> listByUser(@RequestParam Long userId) {
        return bookingService.listBookingsByUser(userId).stream().map(b -> {
            java.math.BigDecimal totalPrice = null;
            if (b.showtime != null && b.showtime.price != null && b.seats != null) {
                totalPrice = b.showtime.price.multiply(java.math.BigDecimal.valueOf(b.seats.size()));
            }
            
            // Use showtime date since bookingDate field was removed
            String displayDate = null;
            if (b.showtime != null && b.showtime.date != null) {
                displayDate = b.showtime.date.toString();
            }
            
            return new BookingDto(
                b.id,
                b.status,
                b.showtime != null && b.showtime.movie != null ? b.showtime.movie.title : null,
                displayDate,
                b.showtime != null && b.showtime.time != null ? b.showtime.time.toString() : null,
                b.seats.stream().map(bs -> bs.seat != null ? bs.seat.seatNumber : null).filter(java.util.Objects::nonNull).toList(),
                totalPrice
            );
        }).toList();
    }
    
    public static record AvailableSeatsDto(String seatNumber, String status) {}
    
    @GetMapping("/showtime/{showtimeId}/available-seats")
    public java.util.List<AvailableSeatsDto> getAvailableSeats(@PathVariable Long showtimeId) {
        return bookingService.getAvailableSeats(showtimeId);
    }
}