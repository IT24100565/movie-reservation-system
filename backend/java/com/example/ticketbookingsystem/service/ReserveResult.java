package com.example.ticketbookingsystem.service;

import java.time.LocalDateTime;
import java.util.List;

public record ReserveResult(String holdToken, LocalDateTime expiry, List<String> seats) {}
