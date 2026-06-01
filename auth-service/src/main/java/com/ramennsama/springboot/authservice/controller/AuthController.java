package com.ramennsama.springboot.authservice.controller;

import com.ramennsama.springboot.authservice.dto.request.LoginRequest;
import com.ramennsama.springboot.authservice.dto.request.RegisterRequest;
import com.ramennsama.springboot.authservice.dto.response.AuthResponse;
import com.ramennsama.springboot.authservice.dto.response.RegisterResponse;
import com.ramennsama.springboot.authservice.service.AuthService;
import com.ramennsama.springboot.authservice.producer.OtpProducerService;
import com.ramennsama.springboot.authservice.dto.event.OtpType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication REST API Endpoints",
        description = "Operations related to register & login"
)
public class AuthController {

    private final AuthService authService;
    private final OtpProducerService otpProducerService;

    @PostMapping("/register")
    @Operation(summary = "Register a user", description = "Create new user in database")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "submit email & password to authenticate user")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh token", description = "Refresh new token")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(authService.refreshToken(authHeader));
    }

    @PostMapping("/test-send-otp")
    @Operation(summary = "Send OTP via Kafka", description = "Push OTP message to Kafka for notification-service to process")
    public ResponseEntity<String> testSendOtp(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam(required = false) String type
    ) {
        OtpType otpType = OtpType.REGISTER_OTP;
        if (type != null) {
            String cleanType = type.replaceAll("[\"']", "").trim().toUpperCase(); // hehe copy sai json bi du dau:V
            try {
                otpType = OtpType.valueOf(cleanType);
            } catch (IllegalArgumentException e) {
                otpType = OtpType.REGISTER_OTP;
            }
        }
        otpProducerService.sendOtp(email, otp, 10, otpType);
        return ResponseEntity.ok("OTP Event sent to Kafka successfully!");
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verify registered user's email with OTP")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp
    ) {
        authService.verifyOtp(email, otp);
        return ResponseEntity.ok("OTP verified successfully. Account is now enabled.");
    }
}
