package com.example.worthmate_backend.auth.service;

import com.example.worthmate_backend.auth.dto.AvailabilityRequest;
import com.example.worthmate_backend.auth.entity.Availability;
import com.example.worthmate_backend.auth.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
@Service
public class AvailabilityService {
    @Autowired
    private AvailabilityRepository availabilityRepository;

    public List<Availability> getAvailableSlots(UUID mentorId) {
        return availabilityRepository.findByMentorIdAndBookedFalse(mentorId);
    }

    public void generateSlots(UUID mentorId, AvailabilityRequest req) {

        LocalTime start = LocalTime.parse(req.getStartTime());
        LocalTime end = LocalTime.parse(req.getEndTime());

        List<DayOfWeek> workingDays = req.getDays()
                .stream()
                .map(DayOfWeek::valueOf)
                .toList();

        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) { // 🔥 increase range

            LocalDate date = today.plusDays(i);
            DayOfWeek day = date.getDayOfWeek();

            if (!workingDays.contains(day)) continue;

            LocalTime time = start;

            while (time.isBefore(end)) {

                boolean exists = availabilityRepository
                        .existsByMentorIdAndDateAndTime(mentorId, date, time);

                if (!exists) {
                    Availability slot = new Availability();
                    slot.setMentorId(mentorId);
                    slot.setDate(date);
                    slot.setTime(time);
                    slot.setBooked(false);

                    availabilityRepository.save(slot);
                }

                time = time.plusMinutes(60); // 🔥 better than 1 hour slots
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void deletePastSlots() {
        availabilityRepository.deleteAllByDateBefore(LocalDate.now());
    }
}
