package com.example.worthmate_backend.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;
@Entity
@Data
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID bookingId;

    private int rating;
    private String review;
}
