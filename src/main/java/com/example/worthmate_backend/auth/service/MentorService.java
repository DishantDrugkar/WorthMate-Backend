package com.example.worthmate_backend.auth.service;

import com.example.worthmate_backend.auth.dto.MentorProfileRequest;
import com.example.worthmate_backend.auth.entity.Mentor;
import com.example.worthmate_backend.auth.repository.MentorRepository;
import com.example.worthmate_backend.auth.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class MentorService {

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Profile Management
    public Mentor createMentor(Mentor mentor) {
        return mentorRepository.save(mentor);
    }

    public Mentor updateMentor(UUID id, Mentor updatedMentor) {
        Mentor mentor = mentorRepository.findById(id).orElseThrow();
        mentor.setFirstName(updatedMentor.getFirstName());
        mentor.setLastName(updatedMentor.getLastName());
        mentor.setEmail(updatedMentor.getEmail());
        mentor.setAvailableSlots(updatedMentor.getAvailableSlots());
        mentor.setRating(updatedMentor.getRating());
        mentor.setTitle(updatedMentor.getTitle());
        mentor.setBio(updatedMentor.getBio());
        mentor.setPasswordHash(updatedMentor.getPasswordHash());
        mentor.setProfilePic(updatedMentor.getProfilePic());
        return mentorRepository.save(mentor);
    }

    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    // Availability Management
    public Mentor updateAvailability(UUID id, List<LocalTime> slots) {
        Mentor mentor = mentorRepository.findById(id).orElseThrow();
        mentor.setAvailableSlots(slots);
        return mentorRepository.save(mentor);
    }

    // Rating Calculation
    public Mentor updateRating(UUID id, Double newRating) {
        Mentor mentor = mentorRepository.findById(id).orElseThrow();
        mentor.setRating(newRating);
        return mentorRepository.save(mentor);
    }

    @Transactional
    public void completeProfile(MentorProfileRequest request, String token) {

        token = token.replace("Bearer ", "");

        String email = jwtTokenProvider.getEmail(token);

        Mentor mentor = mentorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        System.out.println("Skills: " + request.getSkills());
        System.out.println("Experience: " + request.getExperience());
        System.out.println("Rate: " + request.getHourlyRate());
        System.out.println("LinkedIn: " + request.getLinkedin());

        if (request.getSkills() != null) {
            mentor.setSkills(request.getSkills());

            mentor.setExpertise(
                    Arrays.stream(request.getSkills().split(","))
                            .map(String::trim)
                            .toList()
            );
        }

        if (request.getExperience() != null) {
            mentor.setExperience(request.getExperience());
        }

        if (request.getHourlyRate() != null) {
            mentor.setHourlyRate(request.getHourlyRate());
        }

        if (request.getLinkedin() != null) {
            mentor.setLinkedin(request.getLinkedin());
        }
    }

    public Mentor getMentorById(UUID id) {
        return mentorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));
    }
}