package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.model.Payment;
import com.example.ticketbookingsystem.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public static record CreatePaymentRequest(Long bookingId, BigDecimal amount, String method) {}
    public static record CreatePaymentResponse(Long paymentId, String status) {}

    @PostMapping
    public CreatePaymentResponse create(@RequestBody CreatePaymentRequest req) {
        Payment p = paymentService.processPayment(req.bookingId(), req.amount(), req.method());
        return new CreatePaymentResponse(p.id, p.status);
    }
}


