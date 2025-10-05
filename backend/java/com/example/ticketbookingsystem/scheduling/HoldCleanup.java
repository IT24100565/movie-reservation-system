package com.example.ticketbookingsystem.scheduling;

import com.example.ticketbookingsystem.service.BookingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HoldCleanup {
    private final BookingService bookingService;
    public HoldCleanup(BookingService bookingService){ this.bookingService = bookingService; }

    // runs every 30 seconds to free expired holds (demo)
    @Scheduled(fixedRate = 30000)
    public void cleanup() {
        bookingService.releaseExpiredHolds();
    }
}
