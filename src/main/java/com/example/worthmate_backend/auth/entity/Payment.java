package com.example.worthmate_backend.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;
@Entity
@Table(name = "payment")
@Data
public class Payment {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID bookingId;
    private Double amount;
    private String status; // SUCCESS, FAILED

    private String provider; //RAZORPAY
    private String paymentId;

    @Column(name = "order_id")
    private String orderId;
}
