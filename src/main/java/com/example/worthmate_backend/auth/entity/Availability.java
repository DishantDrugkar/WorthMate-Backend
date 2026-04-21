package com.example.worthmate_backend.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
@Entity
@Table(name = "availability")
@Data
public class Availability {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID mentorId;

    private LocalDate date;     // 📅 date
    private LocalTime time;     // ⏰ time

    private boolean booked;
}
