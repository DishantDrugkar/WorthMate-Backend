package com.example.worthmate_backend.auth.service;

import com.example.worthmate_backend.auth.dto.BookingRequest;
import com.example.worthmate_backend.auth.entity.Availability;
import com.example.worthmate_backend.auth.entity.Booking;
import com.example.worthmate_backend.auth.repository.AvailabilityRepository;
import com.example.worthmate_backend.auth.repository.BookingRepository;
import com.example.worthmate_backend.auth.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ✅ CREATE BOOKING
    @Transactional
    public Booking createBooking(BookingRequest request, String token) {

        // 🔐 extract user from token
        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmail(token);

        // ⚠️ ideally DB se userId nikalo (future improvement)
        UUID userId = UUID.nameUUIDFromBytes(email.getBytes());

        // 📦 fetch slot FIRST (IMPORTANT)
        Availability slot = availabilityRepository.findById(request.getAvailabilityId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // ❌ DB level double booking protection
        boolean alreadyBooked = bookingRepository.existsByMentorIdAndDateAndTime(
                slot.getMentorId(),
                slot.getDate(),
                slot.getTime()
        );

        if (alreadyBooked) {
            throw new RuntimeException("Slot already booked");
        }

        // ❌ slot already marked booked
        if (slot.isBooked()) {
            throw new RuntimeException("This slot is already booked");
        }

        // ❌ past date booking block
        if (slot.getDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot book past dates");
        }

        // ✅ mark slot booked
        slot.setBooked(true);
        availabilityRepository.save(slot);

        // ✅ create booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setMentorId(slot.getMentorId());
        booking.setDate(slot.getDate());
        booking.setTime(slot.getTime());
        booking.setStatus("CONFIRMED");

        // 🔗 meeting link generate
        booking.setMeetingLink(generateMeetingLink());

        return bookingRepository.save(booking);
    }

    // ✅ GET BOOKING
    public Booking getBookingById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    // ✅ CANCEL BOOKING
    @Transactional
    public void cancelBooking(UUID bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("CANCELLED");

        // 🔄 free slot again
        Availability slot = availabilityRepository
                .findByMentorIdAndDateAndTime(
                        booking.getMentorId(),
                        booking.getDate(),
                        booking.getTime()
                );

        if (slot != null) {
            slot.setBooked(false);
            availabilityRepository.save(slot);
        }

        bookingRepository.save(booking);
    }

    // 🔗 helper method
    private String generateMeetingLink() {
        return "https://meet.worthmate.com/" + UUID.randomUUID();
    }
}