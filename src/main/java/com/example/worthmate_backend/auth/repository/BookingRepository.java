package com.example.worthmate_backend.auth.repository;

import com.example.worthmate_backend.auth.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUserId(UUID userId);
    List<Booking> findByMentorId(UUID mentorId);
    boolean existsByMentorIdAndDateAndTime(UUID mentorId, java.time.LocalDate date, java.time.LocalTime time);
}
