package com.ramennsama.springboot.authservice.exception;

import com.ramennsama.springboot.authservice.dto.response.DataResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<DataResponse<Object>> handleAppException(AppException exception) {

        ErrorCode errorCode = exception.getErrorCode();
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(errorCode.getHttpStatus().value());
        response.setError(errorCode.getCode());
        response.setMessage(errorCode.getMessage());
        // response.setData(null);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse> handleValidationException(
            MethodArgumentNotValidException exc,
            HttpServletRequest request) {

        // get enum name from DTO annotation (e.g. "EMAIL_REQUIRED")
        String enumName = Optional.ofNullable(exc.getFieldError())
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("INVALID_DATA");

        ErrorCode errorCode;
        try {
            // Dynamically map string to ErrorCode enum
            errorCode = ErrorCode.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            // Fallback if message is not a valid Enum name
            errorCode = ErrorCode.INVALID_DATA;
        }

        DataResponse error = new DataResponse();
        error.setStatus(errorCode.getHttpStatus().value());
        error.setError(errorCode.getCode());
        error.setMessage(errorCode.getMessage());
        error.setData(enumName);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getStatusCode());
        body.put("message", ex.getReason()); // Chỉ lấy message

        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    @ExceptionHandler(BadCredentialsException.class) // error password
    public ResponseEntity<DataResponse<Object>> handleBadCredentialsException(BadCredentialsException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(401);
        response.setError("UNAUTHORIZED");
        response.setMessage("Invalid email or password");

        return ResponseEntity.status(401).body(response);
    }
}
