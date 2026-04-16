package com.example.worthmate_backend.auth.service;

import com.example.worthmate_backend.auth.entity.Feedback;
import com.example.worthmate_backend.auth.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback submitFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }
}
