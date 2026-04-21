package com.example.worthmate_backend.auth.controller;

import com.example.worthmate_backend.auth.dto.BookingRequest;
import com.example.worthmate_backend.auth.entity.Booking;
import com.example.worthmate_backend.auth.security.JwtTokenProvider;
import com.example.worthmate_backend.auth.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // ✅ BOOK SLOT
    @PostMapping
    public ResponseEntity<?> bookSlot(
            @RequestBody BookingRequest request,
            @RequestHeader("Authorization") String token) {

        Booking booking = bookingService.createBooking(request, token);

        return ResponseEntity.ok(Map.of(
                "bookingId", booking.getId()
        ));
    }

    // ✅ GET BOOKING DETAILS
    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }
}