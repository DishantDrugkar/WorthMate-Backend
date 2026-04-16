package com.example.worthmate_backend.auth.entity;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mentors", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
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

    @ElementCollection
    private List<String> expertise;

    public List<String> getExpertise() {
        return expertise;
    }

    public void setExpertise(List<String> expertise) {
        this.expertise = expertise;
    }
// Getters & Setters

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public Integer getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Integer hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return PasswordHash;
    }

    public void setPasswordHash(String passwordHash) {
        PasswordHash = passwordHash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<LocalTime> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<LocalTime> availableSlots) {
        this.availableSlots = availableSlots;
    }
}
