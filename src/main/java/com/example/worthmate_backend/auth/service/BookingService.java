package com.example.worthmate_backend.auth.service;

import com.example.worthmate_backend.auth.entity.Booking;
import com.example.worthmate_backend.auth.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public Booking createBooking(Booking booking) {
        booking.setStatus("PENDING");
        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(UUID userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getMentorBookings(UUID mentorId) {
        return bookingRepository.findByMentorId(mentorId);
    }

    public Booking cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }
}
