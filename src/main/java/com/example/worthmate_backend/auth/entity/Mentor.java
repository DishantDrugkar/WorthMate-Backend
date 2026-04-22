package com.example.worthmate_backend.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mentors", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
public class Mentor {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String PasswordHash;

    @Column
    private String title;

    @Column(length = 1000)
    private String bio;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.MENTOR;

    private Double rating = 0.0;

    @ElementCollection
    private List<LocalTime> availableSlots;

    @Column
    private String skills;

    @Column
    private Integer experience;

    @Column
    private Integer hourlyRate;

    @Column
    private String linkedin;

    @Column(name = "profile_pic")
    private String profilePic;

    @ElementCollection
    private List<String> expertise;

    private String qrCodeUrl;


}
