package com.example.worthmate_backend.auth.controller;

import com.example.worthmate_backend.auth.dto.MentorProfileRequest;
import com.example.worthmate_backend.auth.entity.Mentor;
import com.example.worthmate_backend.auth.service.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/mentors")
public class MentorController {

    @Autowired
    private MentorService mentorService;

    @PostMapping("/create")
    public Mentor createMentor(@RequestBody Mentor mentor) {
        return mentorService.createMentor(mentor);
    }

    @GetMapping
    public List<Mentor> getAllMentors() {
        return mentorService.getAllMentors();
    }

    @PutMapping("/{id}")
    public Mentor updateMentor(@PathVariable UUID id, @RequestBody Mentor mentor) {
        return mentorService.updateMentor(id, mentor);
    }

    @PutMapping("/{id}/availability")
    public Mentor updateAvailability(@PathVariable UUID id,
                                           @RequestBody List<LocalTime> slots) {
        return mentorService.updateAvailability(id, slots);
    }

    @PutMapping("/{id}/rating")
    public Mentor updateRating(@PathVariable UUID id,
                                     @RequestParam Double rating) {
        return mentorService.updateRating(id, rating);
    }

    @PostMapping("/complete-profile")
    public String completeProfile(@RequestBody MentorProfileRequest request,
                                  @RequestHeader("Authorization") String token) {
        mentorService.completeProfile(request, token);
        return "Profile saved successfully";
    }
}
