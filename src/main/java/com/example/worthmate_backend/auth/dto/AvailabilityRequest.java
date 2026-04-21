package com.example.worthmate_backend.auth.dto;

import lombok.Data;

import java.util.List;
@Data
public class AvailabilityRequest {
    private List<String> days; // ["MONDAY", "TUESDAY"]
    private String startTime;  // "18:00"
    private String endTime;
}
