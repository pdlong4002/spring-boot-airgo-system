package com.ramennsama.springboot.authservice.service.impl;

import com.ramennsama.springboot.authservice.dto.request.PasswordUpdateRequest;
import com.ramennsama.springboot.authservice.dto.response.UserResponse;
import com.ramennsama.springboot.authservice.entity.User;
import com.ramennsama.springboot.authservice.enums.Role;
import com.ramennsama.springboot.authservice.repository.UserRepository;
import com.ramennsama.springboot.authservice.service.UserService;
import com.ramennsama.springboot.authservice.utils.FindAuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserResponse getUserInfo() {
        return this.modelMapper.map(this.findAuthenticatedUser.getAuthenticatedUser(), UserResponse.class);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return this.modelMapper.map(user, UserResponse.class);
    }

    @Override
    public void deleteUser() {
        User user = this.findAuthenticatedUser.getAuthenticatedUser();
        if(isLastAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The last Admin cannot delete itself");
        }
        userRepository.delete(user);
    }

    @Override
    public void updatePassword(PasswordUpdateRequest rq) {
        User user = this.findAuthenticatedUser.getAuthenticatedUser();

        if (!isOldPasswordCorrect(user.getPassword(), rq.getOldPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        if (!isNewPasswordConfirmed(rq.getNewPassword(),
                rq.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords do not match");
        }

        if (!isNewPasswordDifferent(rq.getNewPassword(),
                rq.getOldPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old and new passwords must be different");
        }

        user.setPassword(passwordEncoder.encode(rq.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserResponse updateAvatar(String imageUrl) {
        User user = this.findAuthenticatedUser.getAuthenticatedUser();
        user.setImageUrl(imageUrl);
        userRepository.save(user);
        return getUserInfo();
    }

    /**
     * ======================== INTERNAL HELPERS ========================
     */

    private boolean isOldPasswordCorrect(String encodedPassword, String oldPassword) {
        return passwordEncoder.matches(oldPassword, encodedPassword);
    }

    private boolean isNewPasswordConfirmed(String newPassword, String confirmPassword) {
        return confirmPassword.equals(newPassword);
    }

    private boolean isNewPasswordDifferent(String newPassword, String oldPassword) {
        return !newPassword.equals(oldPassword);
    }

    private boolean isLastAdmin(User user) {
        if (user.getRole() != Role.ADMIN) return false;
        long adminCount = userRepository.countByRole(Role.ADMIN);
        return adminCount <= 1;
    }
}
