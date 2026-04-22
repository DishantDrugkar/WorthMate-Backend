package com.example.worthmate_backend.auth.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;

    private String lastName;

    private Integer hourlyRate;

    private String bio;

    private String skills;

    private Integer experience;
}
