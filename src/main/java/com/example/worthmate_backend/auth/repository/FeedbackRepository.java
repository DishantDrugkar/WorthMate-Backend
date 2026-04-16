package com.example.worthmate_backend.auth.repository;

import com.example.worthmate_backend.auth.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
}
