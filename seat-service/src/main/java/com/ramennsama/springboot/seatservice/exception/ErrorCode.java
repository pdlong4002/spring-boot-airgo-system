package com.ramennsama.springboot.seatservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common Errors
    INVALID_KEY("COMMON_001", "Invalid input attributes", HttpStatus.BAD_REQUEST),
    INVALID_DATA("COMMON_002", "Invalid data state", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("COMMON_003", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNCATEGORIZED_EXCEPTION("COMMON_999", "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Seat Specific Errors
    SEAT_NOT_FOUND("SEAT_404", "Seat not found", HttpStatus.NOT_FOUND),
    SEAT_ALREADY_BOOKED("SEAT_409", "Seat already booked", HttpStatus.CONFLICT),
    SEAT_ALREADY_LOCKED("SEAT_410", "Seat is currently locked by another user", HttpStatus.CONFLICT),
    SEAT_ALREADY_EXISTS("SEAT_411", "Seats already exist for this flight", HttpStatus.CONFLICT),
    FLIGHT_NOT_FOUND("SEAT_412", "Flight not found or no seats available", HttpStatus.NOT_FOUND),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}