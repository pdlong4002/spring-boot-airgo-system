package com.ramennsama.springboot.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "OAuth2 Test Endpoints", description = "Endpoints for testing OAuth2 flow without Frontend")
public class OAuth2Controller {

    // để có nút xanh authorize, cần xóa Revoke đi mới dc nhe
    // token from class : OAuth2AuthenticationSuccessHandler
    @GetMapping("/oauth2-success")
    @Operation(summary = "OAuth2 Success Callback", description = "Receives JWT token after successful OAuth2 login")
    public ResponseEntity<Map<String, String>> oauth2Success(@RequestParam("token") String token) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "OAuth2 Login Successful!");
        response.put("token", token);
        response.put("token_type", "Bearer");
        response.put("instruction", "Use this token in Authorization header as 'Bearer <token>' for other requests.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth2-failure")
    @Operation(summary = "OAuth2 Failure Callback", description = "Receives error message after failed OAuth2 login")
    public ResponseEntity<Map<String, String>> oauth2Failure(@RequestParam(value = "error", required = false) String error) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "OAuth2 Login Failed");
        response.put("reason", error != null ? error : "Unknown error");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
