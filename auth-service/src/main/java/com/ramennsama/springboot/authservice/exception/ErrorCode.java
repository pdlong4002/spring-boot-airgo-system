package com.ramennsama.springboot.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_DATA("COMMON_001", "Invalid data", HttpStatus.BAD_REQUEST),
    INVALID_INPUT("USER_999", "Invalid input", HttpStatus.BAD_REQUEST),

    // Authentication
    UNAUTHENTICATED("AUTH_001", "Authentication required", HttpStatus.UNAUTHORIZED),
    BAD_CREDENTIALS("AUTH_002", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH_003", "Account is locked", HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED("AUTH_004", "Account is disabled", HttpStatus.FORBIDDEN),

    // Access Token
    INVALID_ACCESS_TOKEN("AUTH_005", "Access token is invalid", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED("AUTH_006", "Access token has expired", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_REVOKED("AUTH_007", "Access token has been revoked", HttpStatus.UNAUTHORIZED),

    // Refresh Token
    INVALID_REFRESH_TOKEN("AUTH_008", "Refresh token is invalid", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("AUTH_009", "Refresh token has expired", HttpStatus.UNAUTHORIZED),

    TOKEN_MISSING("AUTH_010", "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED),
    AUTH_PROVIDER_NOT_SUPPORTED("AUTH_011", "Authentication provider not supported", HttpStatus.BAD_REQUEST),

    // User & Validation
    USERNAME_REQUIRED("USER_001", "Username is required", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED("USER_002", "Email is required", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED("USER_003", "Password is required", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID("USER_004", "Email format is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT("USER_005", "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("USER_006", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_007", "User already exists", HttpStatus.CONFLICT),
    EMAIL_NOT_FOUND("USER_010", "Email not found", HttpStatus.NOT_FOUND),
    INVALID_OTP("AUTH_012", "Invalid OTP", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED("AUTH_013", "OTP has expired", HttpStatus.BAD_REQUEST),
    ACCOUNT_ALREADY_VERIFIED("AUTH_014", "Account is already verified", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}