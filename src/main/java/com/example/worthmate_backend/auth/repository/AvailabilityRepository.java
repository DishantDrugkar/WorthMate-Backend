package com.example.worthmate_backend.auth.repository;

import com.example.worthmate_backend.auth.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
    List<Availability> findByMentorIdAndBookedFalse(UUID mentorId);
    Availability findByMentorIdAndDateAndTime(
            UUID mentorId,
            java.time.LocalDate date,
            java.time.LocalTime time
    );

    List<Availability> findByMentorIdAndDateAndBookedFalse(
            UUID mentorId,
            LocalDate date
    );

    boolean existsByMentorIdAndDateAndTime(UUID mentorId, LocalDate date, LocalTime time);

    void deleteAllByDateBefore(LocalDate date);
}
