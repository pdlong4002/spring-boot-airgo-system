package com.ramennsama.springboot.apigateway.dto.response;

import lombok.Getter;

@Getter
public class AuthResponse {
    private int status;
    private String message;

    public AuthResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
