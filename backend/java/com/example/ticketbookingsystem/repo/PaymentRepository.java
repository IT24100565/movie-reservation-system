package com.example.ticketbookingsystem.repo;

import com.example.ticketbookingsystem.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}


