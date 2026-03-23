package com.example.worthmate_backend.auth.service;

import com.example.worthmate_backend.auth.entity.Mentor;
import com.example.worthmate_backend.auth.repository.MentorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class MentorService {

    @Autowired
    private MentorRepository mentorRepository;

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
        mentor.setPassword(updatedMentor.getPassword());
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
}