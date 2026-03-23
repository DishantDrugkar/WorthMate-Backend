package com.example.worthmate_backend.auth.service;

import com.example.worthmate_backend.auth.dto.AuthResponse;
import com.example.worthmate_backend.auth.dto.LoginRequest;
import com.example.worthmate_backend.auth.dto.MentorSignUpRequest;
import com.example.worthmate_backend.auth.dto.SignUpRequest;
import com.example.worthmate_backend.auth.entity.Mentor;
import com.example.worthmate_backend.auth.entity.User;
import com.example.worthmate_backend.auth.entity.UserRole;
import com.example.worthmate_backend.auth.repository.MentorRepository;
import com.example.worthmate_backend.auth.repository.UserRepository;
import com.example.worthmate_backend.auth.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MentorRepository mentorRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponse signupUser(SignUpRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        userRepository.save(user);

        // ✅ FIXED
        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId().toString()
        );

        return new AuthResponse(token, user.getRole().name(), user.getId().toString());
    }

    public AuthResponse login(LoginRequest request) {

        // User check
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new RuntimeException("Invalid password");
            }
            String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name(), user.getId().toString());
            return new AuthResponse(token, user.getRole().name(), user.getId().toString());
        }

        // Mentor check
        Optional<Mentor> mentorOpt = mentorRepository.findByEmail(request.getEmail());
        if(mentorOpt.isPresent()) {
            Mentor mentor = mentorOpt.get();
            if(!passwordEncoder.matches(request.getPassword(), mentor.getPassword())) {
                throw new RuntimeException("Invalid password");
            }
            String token = jwtTokenProvider.generateToken(mentor.getEmail(), mentor.getRole().name(), mentor.getId().toString());
            return new AuthResponse(token, "MENTOR", mentor.getId().toString());
        }

        throw new RuntimeException("Invalid email");
    }

    public AuthResponse signupMentor(MentorSignUpRequest request) {
        if (mentorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Mentor mentor = new Mentor();
        mentor.setFirstName(request.getFirstName());
        mentor.setLastName(request.getLastName());
        mentor.setEmail(request.getEmail());
        mentor.setPassword(passwordEncoder.encode(request.getPassword()));
        mentor.setTitle(request.getTitle());
        mentor.setBio(request.getBio());

        mentorRepository.save(mentor);

        String token = jwtTokenProvider.generateToken(mentor.getEmail(), mentor.getRole().name(), mentor.getId().toString());

        return new AuthResponse(token, "MENTOR", mentor.getId().toString());
    }
}