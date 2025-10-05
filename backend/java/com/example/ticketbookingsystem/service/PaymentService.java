package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.Payment;
import com.example.ticketbookingsystem.repo.BookingRepository;
import com.example.ticketbookingsystem.repo.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Payment processPayment(Long bookingId, BigDecimal amount, String method) {
        if (bookingId == null) throw new IllegalArgumentException("bookingId is required");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (method == null || method.isBlank()) method = "CARD";

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Payment payment = new Payment();
        payment.booking = booking;
        payment.amount = amount;
        payment.method = method;
        payment.status = "SUCCESS"; // In a real system this would call a gateway first
        payment.timestamp = LocalDateTime.now();

        paymentRepository.save(payment);
        return payment;
    }
}


