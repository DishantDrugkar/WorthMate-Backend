package com.example.worthmate_backend.auth.repository;

import com.example.worthmate_backend.auth.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsByMentorIdAndDateAndTime(UUID mentorId, java.time.LocalDate date, java.time.LocalTime time);

    Optional<Booking> findByRazorpayOrderId(String razorpayOrderId);

    List<Booking> findByMentorId(UUID mentorId);

    List<Booking> findByUserId(UUID userId);
}
