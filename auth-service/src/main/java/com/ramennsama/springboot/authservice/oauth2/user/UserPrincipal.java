package com.ramennsama.springboot.authservice.oauth2.user;

import com.ramennsama.springboot.authservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ramennsama.springboot.authservice.oauth2.common.OAuth2Constant.ROLE_USER;

// login thuong va OAuth2
@Setter
@AllArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails {
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;  // data từ OAuth2 (Google trả về)

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER));
        return new UserPrincipal(user.getEmail(), user.getPassword(), authorities, Map.of());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return this.email;
    }
}
