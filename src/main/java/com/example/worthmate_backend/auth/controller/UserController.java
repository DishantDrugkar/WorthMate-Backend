package com.example.worthmate_backend.auth.controller;

import com.example.worthmate_backend.auth.dto.*;
import com.example.worthmate_backend.auth.entity.Mentor;
import com.example.worthmate_backend.auth.entity.User;
import com.example.worthmate_backend.auth.repository.MentorRepository;
import com.example.worthmate_backend.auth.repository.UserRepository;
import com.example.worthmate_backend.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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

    // ================= AUTH =================

    @PostMapping("/signup/user")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignUpRequest request) {
        return new ResponseEntity<>(userService.signupUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/signup/mentor")
    public ResponseEntity<AuthResponse> signupMentor(@RequestBody MentorSignUpRequest request) {
        return ResponseEntity.ok(userService.signupMentor(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // ================= GET PROFILE =================

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {

        String email = authentication.getName();

        // 🔥 USER
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            ProfileResponse res = new ProfileResponse();
            res.setFirstName(user.getFirstName());
            res.setLastName(user.getLastName());
            res.setEmail(user.getEmail());
            res.setProfilePic(user.getProfilePic());

            return ResponseEntity.ok(res);
        }

        // 🔥 MENTOR (FULL DATA)
        Optional<Mentor> mentorOpt = mentorRepository.findByEmail(email);
        if (mentorOpt.isPresent()) {
            Mentor m = mentorOpt.get();

            ProfileResponse res = new ProfileResponse();
            res.setFirstName(m.getFirstName());
            res.setLastName(m.getLastName());
            res.setEmail(m.getEmail());
            res.setProfilePic(m.getProfilePic());

            // ✅ EXTRA FIELDS
            res.setHourlyRate(m.getHourlyRate());
            res.setBio(m.getBio());
            res.setSkills(m.getSkills());
            res.setExperience(m.getExperience());
            res.setQrCodeUrl(m.getQrCodeUrl());

            return ResponseEntity.ok(res);
        }

        throw new RuntimeException("User not found");
    }

    // ================= UPDATE PROFILE =================

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @RequestBody UpdateProfileRequest request) {

        String email = authentication.getName();

        // 🔥 MENTOR FIRST (priority)
        Optional<Mentor> mentorOpt = mentorRepository.findByEmail(email);
        if (mentorOpt.isPresent()) {

            Mentor m = mentorOpt.get();

            m.setFirstName(request.getFirstName());
            m.setLastName(request.getLastName());

            // ✅ extra fields
            if (request.getHourlyRate() != null)
                m.setHourlyRate(request.getHourlyRate());

            if (request.getBio() != null)
                m.setBio(request.getBio());

            if (request.getSkills() != null)
                m.setSkills(request.getSkills());

            if (request.getExperience() != null)
                m.setExperience(request.getExperience());

            return ResponseEntity.ok(mentorRepository.save(m));
        }

        // 🔥 USER
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {

            User user = userOpt.get();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());

            return ResponseEntity.ok(userRepository.save(user));
        }

        throw new RuntimeException("User not found");
    }

    // ================= PROFILE PIC =================

    @PostMapping("/upload-profile-pic")
    public ResponseEntity<?> uploadProfilePic(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        return uploadFile(authentication, file, "profile");
    }

    // ================= QR CODE UPLOAD =================

    @PostMapping("/upload-qr")
    public ResponseEntity<?> uploadQr(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        String email = authentication.getName();

        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            File savedFile = new File(uploadDir + fileName);
            file.transferTo(savedFile);

            String qrUrl = "http://localhost:8080/uploads/" + fileName;

            Optional<Mentor> mentorOpt = mentorRepository.findByEmail(email);
            if (mentorOpt.isPresent()) {
                Mentor mentor = mentorOpt.get();
                mentor.setQrCodeUrl(qrUrl);
                mentorRepository.save(mentor);
            }

            return ResponseEntity.ok(Map.of("qrCodeUrl", qrUrl));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed");
        }
    }

    // ================= COMMON FILE UPLOAD =================

    private ResponseEntity<?> uploadFile(Authentication authentication,
                                         MultipartFile file,
                                         String type) {

        String email = authentication.getName();

        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            File savedFile = new File(uploadDir + fileName);
            file.transferTo(savedFile);

            String fileUrl = "http://localhost:8080/uploads/" + fileName;

            // 🔥 USER
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent() && type.equals("profile")) {
                User user = userOpt.get();
                user.setProfilePic(fileUrl);
                userRepository.save(user);
            }

            // 🔥 MENTOR
            Optional<Mentor> mentorOpt = mentorRepository.findByEmail(email);
            if (mentorOpt.isPresent()) {
                Mentor mentor = mentorOpt.get();

                if (type.equals("profile")) {
                    mentor.setProfilePic(fileUrl);
                } else if (type.equals("qr")) {
                    mentor.setQrCodeUrl(fileUrl);
                }

                mentorRepository.save(mentor);
            }

            return ResponseEntity.ok(Map.of("url", fileUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Upload Failed"));
        }
    }
}