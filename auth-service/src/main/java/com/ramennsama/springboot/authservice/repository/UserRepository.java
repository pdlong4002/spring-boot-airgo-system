package com.ramennsama.springboot.authservice.repository;

import com.ramennsama.springboot.authservice.entity.User;
import com.ramennsama.springboot.authservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    long countByRole(Role role);
}
