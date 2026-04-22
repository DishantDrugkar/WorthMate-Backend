package com.example.worthmate_backend.auth.controller;

import com.example.worthmate_backend.auth.dto.BookingRequest;
import com.example.worthmate_backend.auth.entity.Booking;
import com.example.worthmate_backend.auth.entity.Mentor;
import com.example.worthmate_backend.auth.repository.BookingRepository;
import com.example.worthmate_backend.auth.repository.MentorRepository;
import com.example.worthmate_backend.auth.security.JwtTokenProvider;
import com.example.worthmate_backend.auth.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // ✅ BOOK SLOT
    @PostMapping
    public ResponseEntity<?> bookSlot(@RequestBody BookingRequest request, @RequestHeader("Authorization") String token) {

        Booking booking = bookingService.createBooking(request, token);

        return ResponseEntity.ok(Map.of(
                "bookingId", booking.getId()
        ));
    }

    // ✅ GET BOOKING DETAILS
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBooking(@PathVariable UUID bookingId) {

        Booking booking = bookingService.getBookingById(bookingId);

        Mentor mentor = mentorRepository.findById(booking.getMentorId())
                .orElseThrow();

        return ResponseEntity.ok(Map.of(
                "bookingId", booking.getId(),
                "mentorName", mentor.getFirstName() + " " + mentor.getLastName(),
                "mentorQrCode", mentor.getQrCodeUrl(),
                "amount", mentor.getHourlyRate()
        ));
    }

    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<?> getMentorBookings(@PathVariable UUID mentorId) {

        List<Booking> bookings = bookingRepository.findByMentorId(mentorId);

        return ResponseEntity.ok(bookings);
    }

}