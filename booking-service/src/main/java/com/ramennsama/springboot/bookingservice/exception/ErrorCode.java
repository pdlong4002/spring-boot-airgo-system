package com.ramennsama.springboot.bookingservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_KEY("COMMON_001", "Invalid input attributes", HttpStatus.BAD_REQUEST),
    INVALID_DATA("COMMON_002", "Invalid data state", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("COMMON_003", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Flight specific
    FLIGHT_NOT_FOUND("FLIGHT_001", "Flight not found", HttpStatus.NOT_FOUND),
    FLIGHT_CLASS_NOT_FOUND("FLIGHT_002", "Flight class not found", HttpStatus.NOT_FOUND),
    
    // Seat specific
    SEAT_ALREADY_LOCKED("SEAT_410", "Seat is currently locked or booked", HttpStatus.CONFLICT),
    SEAT_SERVICE_UNAVAILABLE("SEAT_503", "Seat service is temporarily unavailable, please try again later", HttpStatus.SERVICE_UNAVAILABLE),
    
    // Booking specific
    BOOKING_NOT_FOUND("BOOKING_001", "Booking not found", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}