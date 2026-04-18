package com.example.worthmate_backend.auth.controller;

import com.example.worthmate_backend.auth.dto.*;
import com.example.worthmate_backend.auth.entity.Mentor;
import com.example.worthmate_backend.auth.entity.User;
import com.example.worthmate_backend.auth.repository.MentorRepository;
import com.example.worthmate_backend.auth.repository.UserRepository;
import com.example.worthmate_backend.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @PostMapping("/signup/user")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignUpRequest request) {
        System.out.println("PASSWORD RECEIVED: " + request.getPassword());
        return new ResponseEntity<>(userService.signupUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/signup/mentor")
    public ResponseEntity<AuthResponse> signupMentor(@RequestBody MentorSignUpRequest request) {
        AuthResponse response = userService.signupMentor(request);
        return ResponseEntity.ok(response);
    }

    // ✅ GET PROFILE
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();

        // 🔥 First check USER
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            ProfileResponse response = new ProfileResponse();
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setEmail(user.getEmail());
            response.setProfilePic(user.getProfilePic());
            return ResponseEntity.ok(response);
        }

        // 🔥 Then check MENTOR
        Optional<Mentor> mentorOpt = mentorRepository.findByEmail(email);
        if (mentorOpt.isPresent()) {
            Mentor mentor = mentorOpt.get();

            ProfileResponse response = new ProfileResponse();
            response.setFirstName(mentor.getFirstName());
            response.setLastName(mentor.getLastName());
            response.setEmail(mentor.getEmail());
            response.setProfilePic(mentor.getProfilePic());
            return ResponseEntity.ok(response);
        }

        throw new RuntimeException("User not found");
    }
    // ✅ UPDATE PROFILE
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @RequestBody UpdateProfileRequest request) {

        String email = authentication.getName();

        // USER update
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            return ResponseEntity.ok(userRepository.save(user));
        }

        // MENTOR update
        Optional<Mentor> mentorOpt = mentorRepository.findByEmail(email);
        if (mentorOpt.isPresent()) {
            Mentor mentor = mentorOpt.get();
            mentor.setFirstName(request.getFirstName());
            mentor.setLastName(request.getLastName());
            return ResponseEntity.ok(mentorRepository.save(mentor));
        }

        throw new RuntimeException("User not found");
    }

    @PostMapping("/upload-profile-pic")
    public ResponseEntity<?> uploadProfilePic(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        String email = authentication.getName();

        try {
            // 🔥 Save file locally (simple version)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String uploadDir =  System.getProperty("user.dir") + "/uploads/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File savedFile = new File(uploadDir + fileName);
            file.transferTo(savedFile);

            String imageUrl = "http://localhost:8080/uploads/" + fileName;

            // 🔥 save to USER or MENTOR
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setProfilePic(imageUrl);
                userRepository.save(user);
            }

            Optional<Mentor> mentorOpt = mentorRepository.findByEmail(email);
            if (mentorOpt.isPresent()) {
                Mentor mentor = mentorOpt.get();
                mentor.setProfilePic(imageUrl);
                mentorRepository.save(mentor);
            }

            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error","Upload Failed"));
        }
    }
}