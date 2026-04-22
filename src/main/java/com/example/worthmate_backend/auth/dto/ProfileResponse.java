package com.example.worthmate_backend.auth.dto;

import lombok.Data;

@Data
public class ProfileResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String profilePic;
    private Integer hourlyRate;
    private String bio;
    private String skills;
    private Integer experience;
    private String qrCodeUrl;
}
