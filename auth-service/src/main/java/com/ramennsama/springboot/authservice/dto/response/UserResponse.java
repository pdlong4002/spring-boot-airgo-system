package com.ramennsama.springboot.authservice.dto.response;

import com.ramennsama.springboot.authservice.enums.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;
    private Role role;
    private String imageUrl;
}
