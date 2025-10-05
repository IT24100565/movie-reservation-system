package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.model.*;
import com.example.ticketbookingsystem.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final SeatRepository seatRepo;
    private final BookingRepository bookingRepo;
    private final ShowtimeRepository showtimeRepo;

    public BookingService(SeatRepository seatRepo, BookingRepository bookingRepo, ShowtimeRepository showtimeRepo) {
        this.seatRepo = seatRepo;
        this.bookingRepo = bookingRepo;
        this.showtimeRepo = showtimeRepo;
    }

    @Transactional
    public ConfirmResult createBooking(Long showtimeId, List<String> seatNumbers, Long userId) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        // Lock seats by hall and numbers
        List<Seat> seats = seatRepo.lockSeatsByHallAndSeatNumbers(showtime.hall.id, seatNumbers);
        Set<String> found = seats.stream().map(s -> s.seatNumber).collect(Collectors.toSet());
        for (String sNum : seatNumbers) {
            if (!found.contains(sNum)) throw new RuntimeException("Seat not found: " + sNum);
        }

        // Ensure seats are available
        for (Seat s : seats) {
            if (Objects.equals(s.status, "BOOKED")) {
                throw new RuntimeException("Seat already booked: " + s.seatNumber);
            }
        }

        // Create booking
        Booking booking = new Booking();
        booking.userId = userId;
        booking.showtime = showtime;
        booking.timestamp = LocalDateTime.now();
        booking.status = "CONFIRMED"; // directly confirm upon creation for this simplified flow

        for (Seat s : seats) {
            s.status = "BOOKED";
            booking.seats.add(new BookingSeat(booking, s));
        }

        bookingRepo.save(booking);
        seatRepo.saveAll(seats);

        return new ConfirmResult(UUID.randomUUID().toString(), booking.id);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        System.out.println("Starting cancelBooking for bookingId: " + bookingId);
        
        // First, let's get a fresh copy of the booking
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Free the seats
        for (BookingSeat bs : booking.seats) {
            if (bs.seat != null) {
                bs.seat.status = "AVAILABLE";
                seatRepo.save(bs.seat);
            }
        }
        
        booking.status = "CANCELLED";
        bookingRepo.save(booking);
        System.out.println("Cancelled booking with id: " + booking.id);
    }

    @Transactional
    public ConfirmResult updateBooking(Long bookingId, List<String> newSeatNumbers) {
        return updateBooking(bookingId, newSeatNumbers, null);
    }
    
    @Transactional
    public ConfirmResult updateBooking(Long bookingId, List<String> newSeatNumbers, Long newShowtimeId) {
        System.out.println("Starting updateBooking for bookingId: " + bookingId);
        
        // First, let's get a fresh copy of the booking
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        System.out.println("Found booking with showtime: " + (booking.showtime != null ? booking.showtime.id : "null"));
        
        // If a new showtime is provided, update it
        Showtime showtime = booking.showtime;
        if (newShowtimeId != null) {
            System.out.println("Updating showtime to: " + newShowtimeId);
            showtime = showtimeRepo.findById(newShowtimeId)
                    .orElseThrow(() -> new RuntimeException("Showtime not found"));
            booking.showtime = showtime;
        } else {
            showtime = booking.showtime;
        }

        // Instead of trying to modify the existing BookingSeat entities,
        // let's just clear them and create new ones
        // First, free the old seats
        for (BookingSeat bs : booking.seats) {
            if (bs.seat != null) {
                bs.seat.status = "AVAILABLE";
                seatRepo.save(bs.seat);
            }
        }
        
        // Clear the booking seats
        booking.seats.clear();
        bookingRepo.save(booking); // Save the booking without seats first

        // Lock and assign new seats
        System.out.println("Locking seats for hall " + showtime.hall.id + " with seat numbers: " + newSeatNumbers);
        List<Seat> newSeats = seatRepo.lockSeatsByHallAndSeatNumbers(showtime.hall.id, newSeatNumbers);
        System.out.println("Locked " + newSeats.size() + " seats");
        
        Set<String> found = newSeats.stream().map(s -> s.seatNumber).collect(Collectors.toSet());
        for (String sNum : newSeatNumbers) {
            if (!found.contains(sNum)) throw new RuntimeException("Seat not found: " + sNum);
        }
        for (Seat s : newSeats) {
            if (Objects.equals(s.status, "BOOKED")) {
                throw new RuntimeException("Seat already booked: " + s.seatNumber);
            }
        }
        for (Seat s : newSeats) {
            s.status = "BOOKED";
            System.out.println("Setting seat " + s.id + " to BOOKED");
        }
        seatRepo.saveAll(newSeats);

        // Create new BookingSeat entities
        for (Seat s : newSeats) {
            BookingSeat bs = new BookingSeat(booking, s);
            booking.seats.add(bs);
            System.out.println("Added seat " + s.seatNumber + " to booking");
        }
        bookingRepo.save(booking);
        System.out.println("Saved booking with id: " + booking.id);
        return new ConfirmResult(UUID.randomUUID().toString(), booking.id);
    }

    @Transactional
    public ConfirmResult simpleUpdateBookingShowtime(Long bookingId, Long newShowtimeId) {
        System.out.println("Starting simpleUpdateBookingShowtime for bookingId: " + bookingId + " to showtime: " + newShowtimeId);
        
        // Get the booking
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Get the new showtime
        Showtime newShowtime = showtimeRepo.findById(newShowtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));
        
        // Update the showtime
        booking.showtime = newShowtime;
        
        // Save the booking
        bookingRepo.save(booking);
        
        System.out.println("Successfully updated booking showtime: " + bookingId);
        return new ConfirmResult(UUID.randomUUID().toString(), booking.id);
    }

    @Transactional
    public ConfirmResult updateBooking(Long bookingId, Long newShowtimeId) {
        System.out.println("Starting updateBooking (showtime only) for bookingId: " + bookingId);
        
        // First, let's get a fresh copy of the booking
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // If a new showtime is provided, update it
        if (newShowtimeId != null) {
            System.out.println("Updating showtime to: " + newShowtimeId);
            Showtime newShowtime = showtimeRepo.findById(newShowtimeId)
                    .orElseThrow(() -> new RuntimeException("Showtime not found"));
            
            // Check if the seats are available for the new showtime
            List<String> currentSeatNumbers = new ArrayList<>();
            for (BookingSeat bs : booking.seats) {
                if (bs.seat != null) {
                    currentSeatNumbers.add(bs.seat.seatNumber);
                }
            }
            
            // Get seats for the new showtime's hall that match our current seat numbers
            List<Seat> newShowtimeSeats = seatRepo.lockSeatsByHallAndSeatNumbers(newShowtime.hall.id, currentSeatNumbers);
            
            // Verify we found all the seats we're looking for
            if (newShowtimeSeats.size() != currentSeatNumbers.size()) {
                throw new RuntimeException("Some seats are not available in the new showtime's hall");
            }
            
            // Check if any of these seats are already booked for the new showtime
            for (Seat seat : newShowtimeSeats) {
                if ("BOOKED".equals(seat.status)) {
                    throw new RuntimeException("Seat " + seat.seatNumber + " is already booked for the selected showtime");
                }
            }
            
            // Update the booking's showtime
            booking.showtime = newShowtime;
        }

        bookingRepo.save(booking);
        System.out.println("Saved booking with id: " + booking.id);
        return new ConfirmResult(UUID.randomUUID().toString(), booking.id);
    }

    public void releaseExpiredHolds() {
    }

    public java.util.List<Booking> listBookingsByUser(Long userId) {
        return bookingRepo.findByUserId(userId).stream()
                .filter(booking -> !"CANCELLED".equals(booking.status))
                .toList();
    }
    
    public java.util.List<com.example.ticketbookingsystem.controller.BookingController.AvailableSeatsDto> getAvailableSeats(Long showtimeId) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));
        
        List<Seat> seats = seatRepo.findByHallId(showtime.hall.id);
        
        return seats.stream()
                .map(seat -> new com.example.ticketbookingsystem.controller.BookingController.AvailableSeatsDto(
                        seat.seatNumber, 
                        seat.status))
                .toList();
    }
    
    @Transactional
    public ConfirmResult updateBookingWithSeatsAndShowtime(Long bookingId, List<String> newSeatNumbers, Long newShowtimeId) {
        try {
            System.out.println("=== STARTING UPDATE BOOKING ===");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("New Seat Numbers: " + newSeatNumbers);
            System.out.println("New Showtime ID: " + newShowtimeId);
            
            // Step 1: Get booking without complex fetching
            System.out.println("Step 1: Getting booking...");
            Booking booking = bookingRepo.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
            System.out.println("Found booking with showtime ID: " + (booking.showtime != null ? booking.showtime.id : "null"));
            
            // Step 2: Handle showtime update if needed
            Showtime showtime;
            if (newShowtimeId != null) {
                System.out.println("Step 2: Updating showtime to " + newShowtimeId);
                showtime = showtimeRepo.findById(newShowtimeId)
                        .orElseThrow(() -> new RuntimeException("Showtime not found: " + newShowtimeId));
                booking.showtime = showtime;
            } else {
                showtime = booking.showtime;
                System.out.println("Step 2: Keeping existing showtime " + (showtime != null ? showtime.id : "null"));
            }
            
            // Step 3: Free current seats using direct database operations
            System.out.println("Step 3: Freeing current seats...");
            if (booking.seats != null && !booking.seats.isEmpty()) {
                System.out.println("Current seats count: " + booking.seats.size());
                for (BookingSeat bs : booking.seats) {
                    if (bs.seat != null) {
                        System.out.println("Freeing seat: " + bs.seat.seatNumber);
                        // Update seat status directly
                        int updated = seatRepo.updateSeatStatus(bs.seat.id, "AVAILABLE");
                        System.out.println("Updated seat status, rows affected: " + updated);
                    }
                }
            } else {
                System.out.println("No current seats to free");
            }
            
            // Step 4: Clear booking seats (but don't save yet)
            System.out.println("Step 4: Clearing booking seats...");
            booking.seats.clear();
            
            // Step 5: Lock and validate new seats
            System.out.println("Step 5: Locking new seats...");
            if (newSeatNumbers != null && !newSeatNumbers.isEmpty()) {
                System.out.println("Locking seats for hall " + showtime.hall.id + ": " + newSeatNumbers);
                List<Seat> newSeats = seatRepo.lockSeatsByHallAndSeatNumbers(showtime.hall.id, newSeatNumbers);
                System.out.println("Locked seats count: " + newSeats.size());
                
                // Validate we found all seats
                Set<String> foundSeats = newSeats.stream().map(s -> s.seatNumber).collect(Collectors.toSet());
                for (String seatNumber : newSeatNumbers) {
                    if (!foundSeats.contains(seatNumber)) {
                        throw new RuntimeException("Seat not found in hall: " + seatNumber);
                    }
                }
                
                // Validate all seats are available
                for (Seat seat : newSeats) {
                    if ("BOOKED".equals(seat.status)) {
                        throw new RuntimeException("Seat already booked: " + seat.seatNumber);
                    }
                }
                
                // Book the new seats
                System.out.println("Booking new seats...");
                for (Seat seat : newSeats) {
                    int updated = seatRepo.updateSeatStatus(seat.id, "BOOKED");
                    System.out.println("Booked seat " + seat.seatNumber + ", rows affected: " + updated);
                }
                
                // Create new booking-seat relationships
                System.out.println("Creating booking-seat relationships...");
                for (Seat seat : newSeats) {
                    BookingSeat bookingSeat = new BookingSeat();
                    bookingSeat.booking = booking;
                    bookingSeat.seat = seat;
                    booking.seats.add(bookingSeat);
                    System.out.println("Added relationship for seat: " + seat.seatNumber);
                }
            }
            
            // Step 6: Save the booking
            System.out.println("Step 6: Saving booking...");
            Booking savedBooking = bookingRepo.save(booking);
            System.out.println("Saved booking with ID: " + savedBooking.id);
            
            System.out.println("=== UPDATE BOOKING COMPLETED SUCCESSFULLY ===");
            return new ConfirmResult(UUID.randomUUID().toString(), savedBooking.id);
        } catch (Exception e) {
            System.out.println("=== UPDATE BOOKING FAILED ===");
            e.printStackTrace();
            throw new RuntimeException("Failed to update booking: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public ConfirmResult simpleUpdateBooking(Long bookingId, Long newShowtimeId, List<String> newSeatNumbers) {
        System.out.println("=== STARTING SIMPLE UPDATE BOOKING ===");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("New Showtime ID: " + newShowtimeId);
        System.out.println("New Seat Numbers: " + newSeatNumbers);
        
        try {
            // Step 1: Get the booking with all necessary data using a direct query
            System.out.println("Step 1: Getting booking with details...");
            Booking booking = bookingRepo.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
            
            // Ensure we have the showtime and hall information
            if (booking.showtime == null) {
                throw new RuntimeException("Booking has no showtime assigned");
            }
            
            System.out.println("Current showtime ID: " + booking.showtime.id);
            System.out.println("Current hall ID: " + (booking.showtime.hall != null ? booking.showtime.hall.id : "null"));
            
            // Step 2: Handle showtime update if needed
            Showtime newShowtime = booking.showtime; // Default to current showtime
            if (newShowtimeId != null && !newShowtimeId.equals(booking.showtime.id)) {
                System.out.println("Step 2: Updating showtime to " + newShowtimeId);
                newShowtime = showtimeRepo.findById(newShowtimeId)
                        .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + newShowtimeId));
                
                // Update the booking's showtime
                booking.showtime = newShowtime;
            } else {
                System.out.println("Step 2: Keeping existing showtime");
            }
            
            // Step 3: Handle seat updates if needed
            if (newSeatNumbers != null && !newSeatNumbers.isEmpty()) {
                System.out.println("Step 3: Updating seats...");
                
                // First, release current seats if any
                if (booking.seats != null && !booking.seats.isEmpty()) {
                    System.out.println("Releasing current seats: " + booking.seats.size());
                    for (BookingSeat bs : booking.seats) {
                        if (bs.seat != null) {
                            System.out.println("Releasing seat: " + bs.seat.seatNumber);
                            bs.seat.status = "AVAILABLE";
                            seatRepo.save(bs.seat);
                        }
                    }
                }
                
                // Clear the booking seats collection
                booking.seats.clear();
                
                // Validate and book new seats
                System.out.println("Booking new seats in hall " + newShowtime.hall.id + ": " + newSeatNumbers);
                List<Seat> seatsToBook = seatRepo.findByHallIdAndSeatNumberIn(newShowtime.hall.id, newSeatNumbers);
                
                // Check if we found all requested seats
                if (seatsToBook.size() != newSeatNumbers.size()) {
                    Set<String> foundSeatNumbers = seatsToBook.stream()
                            .map(s -> s.seatNumber)
                            .collect(Collectors.toSet());
                    
                    for (String requestedSeat : newSeatNumbers) {
                        if (!foundSeatNumbers.contains(requestedSeat)) {
                            throw new RuntimeException("Seat not found in hall: " + requestedSeat);
                        }
                    }
                }
                
                // Check if all seats are available
                for (Seat seat : seatsToBook) {
                    if ("BOOKED".equals(seat.status)) {
                        throw new RuntimeException("Seat is already booked: " + seat.seatNumber);
                    }
                }
                
                // Book the new seats and create booking-seat relationships
                for (Seat seat : seatsToBook) {
                    System.out.println("Booking seat: " + seat.seatNumber);
                    seat.status = "BOOKED";
                    seatRepo.save(seat);
                    
                    // Create booking-seat relationship
                    BookingSeat bookingSeat = new BookingSeat();
                    bookingSeat.booking = booking;
                    bookingSeat.seat = seat;
                    booking.seats.add(bookingSeat);
                }
            } else {
                System.out.println("Step 3: No seat changes requested");
            }
            
            // Step 4: Save the updated booking
            System.out.println("Step 4: Saving booking...");
            Booking savedBooking = bookingRepo.save(booking);
            System.out.println("Saved booking with ID: " + savedBooking.id);
            
            System.out.println("=== UPDATE BOOKING COMPLETED SUCCESSFULLY ===");
            return new ConfirmResult(UUID.randomUUID().toString(), savedBooking.id);
            
        } catch (Exception e) {
            System.out.println("=== UPDATE BOOKING FAILED ===");
            e.printStackTrace();
            throw new RuntimeException("Failed to update booking: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public ConfirmResult verySimpleUpdateBookingShowtime(Long bookingId, Long newShowtimeId) {
        System.out.println("=== STARTING VERY SIMPLE SHOWTIME UPDATE ===");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("New Showtime ID: " + newShowtimeId);
        
        try {
            // Step 1: Get the booking
            System.out.println("Step 1: Getting booking...");
            Booking booking = bookingRepo.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
            
            // Step 2: Get the new showtime
            System.out.println("Step 2: Getting new showtime...");
            Showtime newShowtime = showtimeRepo.findById(newShowtimeId)
                    .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + newShowtimeId));
            
            // Step 3: Update the booking's showtime
            System.out.println("Step 3: Updating showtime...");
            booking.showtime = newShowtime;
            
            // Step 4: Save the booking
            System.out.println("Step 4: Saving booking...");
            Booking savedBooking = bookingRepo.save(booking);
            System.out.println("Saved booking with ID: " + savedBooking.id);
            
            System.out.println("=== SHOWTIME UPDATE COMPLETED SUCCESSFULLY ===");
            return new ConfirmResult(UUID.randomUUID().toString(), savedBooking.id);
            
        } catch (Exception e) {
            System.out.println("=== SHOWTIME UPDATE FAILED ===");
            e.printStackTrace();
            throw new RuntimeException("Failed to update booking showtime: " + e.getMessage(), e);
        }
    }
}