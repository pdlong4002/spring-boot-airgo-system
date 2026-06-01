package com.ramennsama.springboot.seatservice.exception;

import com.ramennsama.springboot.seatservice.dto.response.DataResponse;
import com.ramennsama.springboot.seatservice.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

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

        String enumName = Optional.ofNullable(exc.getFieldError())
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("INVALID_KEY");

        ErrorCode errorCode;
        try {
            errorCode = ErrorCode.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            errorCode = ErrorCode.INVALID_KEY;
        }

        DataResponse error = new DataResponse();
        error.setStatus(errorCode.getHttpStatus().value());
        error.setError(errorCode.getCode());
        error.setMessage(errorCode.getMessage());

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

}
