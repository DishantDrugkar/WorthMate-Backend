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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
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

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ================= BOOK SLOT =================
    @PostMapping
    public ResponseEntity<?> bookSlot(
            @RequestBody BookingRequest request,
            @RequestHeader("Authorization") String token
    ) {

        Booking booking = bookingService.createBooking(request, token);

        return ResponseEntity.ok(Map.of(
                "bookingId", booking.getId(),
                "message", "Booking created successfully"
        ));
    }

    // ================= GET BOOKING =================
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBooking(@PathVariable UUID bookingId) {

        try {
            Booking booking = bookingService.getBookingById(bookingId);

            Mentor mentor = mentorRepository.findById(booking.getMentorId())
                    .orElseThrow(() -> new RuntimeException("Mentor not found"));

            return ResponseEntity.ok(Map.of(
                    "id", booking.getId(),
                    "mentorName", mentor.getFirstName() + " " + mentor.getLastName(),
                    "mentorQrCode", mentor.getQrCodeUrl(),
                    "amount", mentor.getHourlyRate(),
                    "paid", booking.getPaid(),
                    "confirmed", booking.getConfirmed(),
                    "availabilityId", booking.getAvailabilityId(),
                    "userId", booking.getUserId(),
                    "mentorId", booking.getMentorId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }


    // ================= MENTOR BOOKINGS =================
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<?> getMentorBookings(@PathVariable UUID mentorId) {

        List<Booking> bookings = bookingRepository.findByMentorId(mentorId);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Booking> filtered = bookings.stream()
                .filter(b ->
                        b.getDate() != null &&
                                (b.getDate().isAfter(today) ||
                                        (b.getDate().isEqual(today) && b.getTime().isAfter(now)))
                )
                .toList();

        return ResponseEntity.ok(filtered);
    }

    // ================= USER BOOKINGS (FIXED) =================
    @GetMapping("/user")
    public ResponseEntity<?> getUserBookings(
            @RequestHeader("Authorization") String token
    ) {

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmail(token);

        UUID userId = UUID.nameUUIDFromBytes(email.getBytes());

        List<Booking> bookings = bookingRepository.findByUserId(userId);

        return ResponseEntity.ok(bookings);
    }
    // ================= CONFIRM PAYMENT =================
    @PostMapping("/confirm/{bookingId}")
    public ResponseEntity<?> confirmBooking(@PathVariable UUID bookingId) {

        System.out.println("CONFIRM API HIT: " + bookingId);

        Booking booking = bookingService.getBookingById(bookingId);

        if (booking == null) {
            System.out.println("BOOKING NULL");
            return ResponseEntity.status(404).body(Map.of("message", "Booking not found"));
        }

        System.out.println("BEFORE UPDATE: " + booking.getConfirmed());

        booking.setPaid(true);
        booking.setConfirmed(true);

        Booking saved = bookingRepository.save(booking);

        System.out.println("AFTER SAVE: " + saved.getConfirmed());

        return ResponseEntity.ok(Map.of(
                "message", "Booking confirmed",
                "confirmed", saved.getConfirmed()
        ));
    }
}