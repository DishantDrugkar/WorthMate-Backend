package com.example.worthmate_backend.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID bookingId;
    private Double amount;
    private String status; // SUCCESS, FAILED

    private String provider; // STRIPE, RAZORPAY
}
