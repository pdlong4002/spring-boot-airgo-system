package com.ramennsama.springboot.flightservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_DATA("COMMON_001", "Invalid data", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("COMMON_003", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Flight specific (can add more later)
    FLIGHT_NOT_FOUND("FLIGHT_001", "Flight not found", HttpStatus.NOT_FOUND),
    FLIGHT_CLASS_NOT_FOUND("FLIGHT_002", "Flight class not found", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}