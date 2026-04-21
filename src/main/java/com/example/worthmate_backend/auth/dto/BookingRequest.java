package com.example.worthmate_backend.auth.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class BookingRequest {
    private UUID availabilityId;
    private String problemCategory;
    private String problemDescription;
}
