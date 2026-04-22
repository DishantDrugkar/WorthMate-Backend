package com.example.worthmate_backend.auth.controller;

import com.example.worthmate_backend.auth.dto.AvailabilityRequest;
import com.example.worthmate_backend.auth.entity.Availability;
import com.example.worthmate_backend.auth.repository.AvailabilityRepository;
import com.example.worthmate_backend.auth.security.JwtTokenProvider;
import com.example.worthmate_backend.auth.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/mentors")
public class AvailabilityController {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AvailabilityService availabilityService;

    @GetMapping("/{mentorId}/availability")
    public List<Availability> getAvailability(
            @PathVariable UUID mentorId,
            @RequestParam LocalDate date) {

        return availabilityRepository.findByMentorIdAndDateAndBookedFalse(mentorId, date);
    }

    @PostMapping("/availability")
    public Availability addSlot(@RequestBody Availability availability) {
        return availabilityRepository.save(availability);
    }

    @PostMapping("/availability/generate")
    public String generateSlots(
            @RequestBody AvailabilityRequest request,
            @RequestHeader("Authorization") String token
    ) {
        token = token.replace("Bearer ", "");
        UUID mentorId = UUID.fromString(jwtTokenProvider.getUserId(token));

        availabilityService.generateSlots(mentorId, request);

        return "Slots generated successfully";
    }

    @GetMapping("/{mentorId}/availability/all")
    @PreAuthorize("hasRole('MENTOR')")
    public List<Availability> getAllSlots(@PathVariable UUID mentorId) {
        return availabilityRepository.findByMentorId(mentorId);
    }

    @GetMapping("/{mentorId}/availability/upcoming")
    public List<Availability> getUpcomingSlots(@PathVariable UUID mentorId) {
        return availabilityRepository.findByMentorIdAndDateAfter(
                mentorId,
                LocalDate.now().minusDays(1)
        );
    }

    @DeleteMapping("/availability/{slotId}")
    @PreAuthorize("hasRole('MENTOR')")
    public String deleteSlot(
            @PathVariable UUID slotId,
            @RequestHeader("Authorization") String token
    ) {
        token = token.replace("Bearer ", "");
        UUID mentorId = UUID.fromString(jwtTokenProvider.getUserId(token));

        Availability slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // 🔐 Security check (mentor apna hi slot delete kare)
        if (!slot.getMentorId().equals(mentorId)) {
            throw new RuntimeException("Unauthorized");
        }

        availabilityRepository.deleteById(slotId);

        return "Slot deleted successfully";
    }
}
