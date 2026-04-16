package com.example.worthmate_backend.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;
    private UUID mentorId;

    private LocalDateTime bookingTime;

    private String status; // PENDING, CONFIRMED, CANCELLED

    private String meetingLink;

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters & setters
}