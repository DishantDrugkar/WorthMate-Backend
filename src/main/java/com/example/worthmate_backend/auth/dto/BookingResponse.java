package com.example.worthmate_backend.auth.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
@Data
public class BookingResponse {
    private UUID id;
    private String mentorName;
    private String meetingLink;
    private LocalDate date;
    private LocalTime time;
}
