package com.example.worthmate_backend.auth.repository;

import com.example.worthmate_backend.auth.entity.Availability;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
    List<Availability> findByMentorIdAndBookedFalse(UUID mentorId);
    Availability findByMentorIdAndDateAndTime(UUID mentorId, java.time.LocalDate date, java.time.LocalTime time);

    List<Availability> findByMentorIdAndDateAndBookedFalse(UUID mentorId, LocalDate date);

    boolean existsByMentorIdAndDateAndTime(UUID mentorId, LocalDate date, LocalTime time);

    void deleteAllByDateBefore(LocalDate date);

    List<Availability> findByMentorId(UUID mentorId);

    List<Availability> findByMentorIdAndDateAfter(UUID mentorId, LocalDate date);

    @Modifying
    @Transactional
    @Query("UPDATE Availability a SET a.booked = true WHERE a.id = :id")
    void markBooked(@Param("id") UUID id);
}
