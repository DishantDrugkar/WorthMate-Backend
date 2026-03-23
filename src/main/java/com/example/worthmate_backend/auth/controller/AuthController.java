package com.example.worthmate_backend.auth.controller;

import com.example.worthmate_backend.auth.dto.AuthResponse;
import com.example.worthmate_backend.auth.dto.LoginRequest;
import com.example.worthmate_backend.auth.dto.MentorSignUpRequest;
import com.example.worthmate_backend.auth.dto.SignUpRequest;
import com.example.worthmate_backend.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup/user")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignUpRequest request) {
        return new ResponseEntity<>(authService.signupUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/signup/mentor")
    public ResponseEntity<AuthResponse> signupMentor(@RequestBody MentorSignUpRequest request) {
        AuthResponse response = authService.signupMentor(request);
        return ResponseEntity.ok(response);
    }
}