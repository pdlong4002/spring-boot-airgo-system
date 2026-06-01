package com.ramennsama.springboot.authservice.service;

import com.ramennsama.springboot.authservice.dto.request.LoginRequest;
import com.ramennsama.springboot.authservice.dto.request.RegisterRequest;
import com.ramennsama.springboot.authservice.dto.response.AuthResponse;
import com.ramennsama.springboot.authservice.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);

    AuthResponse refreshToken(String authHeader);

    void verifyOtp(String email, String otp);
}
