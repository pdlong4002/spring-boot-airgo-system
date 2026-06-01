package com.ramennsama.springboot.authservice.service.impl;

import com.ramennsama.springboot.authservice.dto.request.LoginRequest;
import com.ramennsama.springboot.authservice.dto.request.RegisterRequest;
import com.ramennsama.springboot.authservice.dto.response.AuthResponse;
import com.ramennsama.springboot.authservice.dto.response.RegisterResponse;
import com.ramennsama.springboot.authservice.entity.User;
import com.ramennsama.springboot.authservice.enums.Role;
import com.ramennsama.springboot.authservice.exception.AppException;
import com.ramennsama.springboot.authservice.exception.ErrorCode;
import com.ramennsama.springboot.authservice.dto.event.OtpType;
import com.ramennsama.springboot.authservice.producer.OtpProducerService;
import com.ramennsama.springboot.authservice.repository.UserRepository;
import com.ramennsama.springboot.authservice.service.AuthService;
import com.ramennsama.springboot.authservice.service.JwtService;
import com.ramennsama.springboot.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int TOKEN_INDEX = 7;

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final OtpProducerService otpProducerService;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        String userName = registerRequest.getUsername();

        Optional<User> userFoundByEmail = userRepository.findByEmail(email);
        Optional<User> userFoundByUsername = userRepository.findByUsername(userName);

        if (userFoundByEmail.isPresent() || userFoundByUsername.isPresent())
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);

        User user = buildNewUser(registerRequest);

        // Generate OTP
        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // Valid for 5 minutes

        userRepository.save(user);

        // Send OTP via Kafka for notification-service to email it
        log.info("Sending registration OTP for email: {}", email);
        otpProducerService.sendOtp(email, otp, 5, OtpType.REGISTER_OTP);
        
        return this.modelMapper.map(user, RegisterResponse.class);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        
        // nếu để lên trc authenticate, nó sẽ gent ra exception này, ko thì sẽ là BadCredentialsException
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        // generate token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // return response
        return AuthResponse.builder()
                .message("Login successfully")
                .email(user.getEmail())
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(String authHeader) {
        // Check header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.TOKEN_MISSING);
        }

        // Get token
        String refreshToken = authHeader.substring(TOKEN_INDEX);

        // Extract email from token
        String email = jwtService.extractUsername(refreshToken);

        if (!StringUtils.hasText(email)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Load user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        // Validate refresh token
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        user.setAccessToken(accessToken);
        user.setRefreshToken(newRefreshToken);

        return AuthResponse.builder()
                .message("Token refreshed successfully")
                .email(user.getEmail())
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private User buildNewUser(RegisterRequest registerRequest) {
        boolean isFirstUser = this.userRepository.count() == 0;
        return User.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .enabled(false)
                .imageUrl(registerRequest.getUrlImage())
                .role(isFirstUser == true ? Role.ADMIN : Role.USER)
                //.role(Role.valueOf(registerRequest.getRole()))
                .build();
    }

    @Override
    public void verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        if (Boolean.TRUE.equals(user.getEnabled())) {
            throw new AppException(ErrorCode.ACCOUNT_ALREADY_VERIFIED);
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        if (user.getOtpCode() == null || !user.getOtpCode().equals(otp)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        user.setEnabled(true);
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        log.info("User {} verified successfully and enabled", email);
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit random code
    }
}
