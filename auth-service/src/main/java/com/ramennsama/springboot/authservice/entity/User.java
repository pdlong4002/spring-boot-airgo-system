package com.ramennsama.springboot.authservice.entity;

import com.ramennsama.springboot.authservice.enums.Role;
import com.ramennsama.springboot.authservice.oauth2.common.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 100, nullable = true)
    private String username;

    @Column(name = "firstname", length = 100, nullable = true)
    private String firstName;

    @Column(name = "lastname", length = 100, nullable = true)
    private String lastName;

    @Column(name = "email", length = 250, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 150)
    private String password;

    @Column(name = "enabled", nullable = true)
    private Boolean enabled;

    @Column(name = "otp_code", length = 10, nullable = true)
    private String otpCode;

    @Column(name = "otp_expiry", nullable = true)
    private LocalDateTime otpExpiry;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private Date createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private Date updateAt;

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.enabled);
    }

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "access_token", length = 500, nullable = true)
    private String accessToken;

    @Column(name = "refresh_token", length = 500, nullable = true)
    private String refreshToken;

    @Column(name = "provider", columnDefinition =
            "ENUM('local', 'facebook', 'google', 'github') DEFAULT 'local'")
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "provider_id", length = 150, nullable = true)
    private String providerId;

    @Column(name = "image_url", length = 500, nullable = true)
    private String imageUrl;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
