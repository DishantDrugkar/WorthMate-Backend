package com.example.worthmate_backend.auth.repository;

import com.example.worthmate_backend.auth.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MentorRepository extends JpaRepository<Mentor, UUID> {
    boolean existsByEmail(String email);
    Optional<Mentor> findByEmail(String email);
}
