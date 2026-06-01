package com.ramennsama.springboot.authservice.dto.response;

import com.ramennsama.springboot.authservice.enums.Role;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    //private String username;
    private String email;
    private String imageUrl;
    private Boolean enabled;
    private Role role;
}
