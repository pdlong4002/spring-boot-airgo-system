package com.ramennsama.springboot.authservice.service;

import com.ramennsama.springboot.authservice.dto.request.PasswordUpdateRequest;
import com.ramennsama.springboot.authservice.dto.response.UserResponse;
import com.ramennsama.springboot.authservice.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    UserResponse getUserInfo();
    UserResponse getUserById(Long userId);
    void deleteUser();
    void updatePassword(PasswordUpdateRequest rq);
    UserResponse updateAvatar(String avatarUrl);
}
