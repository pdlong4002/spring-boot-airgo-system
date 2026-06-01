package com.ramennsama.springboot.authservice.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {
    String generateAccessToken(UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
    String generateToken(Map<String, Object> claims, UserDetails userDetails, Long expiration);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}
