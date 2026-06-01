package com.ramennsama.springboot.authservice.controller;

import com.ramennsama.springboot.authservice.dto.request.PasswordUpdateRequest;
import com.ramennsama.springboot.authservice.dto.response.UserResponse;
import com.ramennsama.springboot.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(
        name = "User REST API Endpoints",
        description = "Operations related to user profile and management"
)
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user info", description = "Retrieve profile of the currently logged-in user")
    public ResponseEntity<UserResponse> getMyInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

//    @GetMapping("/{userId}")
//    @Operation(summary = "Get user by ID", description = "Retrieve user details by their unique ID")
//    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
//        return ResponseEntity.ok(userService.getUserById(userId));
//    }

    @PutMapping("/password")
    @Operation(summary = "Update password", description = "Change the password for the currently logged-in user")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(request);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PutMapping("/avatar")
    @Operation(summary = "Update avatar", description = "Update the profile image URL for the current user")
    public ResponseEntity<UserResponse> updateAvatar(@RequestBody String imageUrl) {
        return ResponseEntity.ok(userService.updateAvatar(imageUrl));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete account", description = "Delete the currently logged-in user's account")
    public ResponseEntity<String> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.ok("User deleted successfully");
    }
}
