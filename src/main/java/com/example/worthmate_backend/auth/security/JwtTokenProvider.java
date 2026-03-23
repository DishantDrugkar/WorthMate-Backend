package com.example.worthmate_backend.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String SECRET = "this-is-a-very-secure-secret-key-123456";
    private final long EXPIRATION = 86400000; // 1 day

    // 🔑 Generate Key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // 🔐 Generate Token (FIXED)
    public String generateToken(String email, String role, String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔍 Extract Email
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 🔍 Extract Role
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 🔍 Extract UserId
    public String getUserId(String token) {
        return getClaims(token).get("userId", String.class);
    }

    // 🔍 Extract Claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Validate Token
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}