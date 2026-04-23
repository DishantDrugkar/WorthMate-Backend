package com.example.worthmate_backend.auth.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class BookingRequest {

    private UUID availabilityId;
    private UUID mentorId;
    private Double amount;

    private LocalDate date;
    private LocalTime time;
}