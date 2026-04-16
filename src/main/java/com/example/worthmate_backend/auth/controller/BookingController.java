package com.example.worthmate_backend.auth.controller;

import com.example.worthmate_backend.auth.entity.Booking;
import com.example.worthmate_backend.auth.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public Booking create(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @GetMapping("/user/{userId}")
    public List<Booking> userBookings(@PathVariable UUID userId) {
        return bookingService.getUserBookings(userId);
    }

    @GetMapping("/mentor/{mentorId}")
    public List<Booking> mentorBookings(@PathVariable UUID mentorId) {
        return bookingService.getMentorBookings(mentorId);
    }

    @PutMapping("/{bookingId}/cancel")
    public Booking cancel(@PathVariable UUID bookingId) {
        return bookingService.cancelBooking(bookingId);
    }
}
