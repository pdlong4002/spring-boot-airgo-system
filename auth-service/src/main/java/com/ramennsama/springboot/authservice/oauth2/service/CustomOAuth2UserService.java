package com.ramennsama.springboot.authservice.oauth2.service;

import com.ramennsama.springboot.authservice.entity.User;
import com.ramennsama.springboot.authservice.enums.Role;
import com.ramennsama.springboot.authservice.oauth2.common.AuthProvider;
import com.ramennsama.springboot.authservice.oauth2.user.OAuth2UserInfo;
import com.ramennsama.springboot.authservice.oauth2.user.OAuth2UserInfoFactory;
import com.ramennsama.springboot.authservice.oauth2.user.UserPrincipal;
import com.ramennsama.springboot.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);

        // Tìm user theo username hoặc email để tránh trùng lặp
        Optional<User> userOptional = userRepository.findByUsername(oAuth2UserInfo.getName());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Kiểm tra provider để tránh việc dùng 1 email đăng nhập nhiều provider khác nhau không đúng luồng
            String providerName = oAuth2UserRequest.getClientRegistration().getRegistrationId();
            if (user.getProvider() != null && !user.getProvider().name().equalsIgnoreCase(providerName)) {
                throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_provider"), 
                        "Looks like you're signed up with " + user.getProvider() +
                        " account. Please use your " + user.getProvider() + " account to login");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user);
    }

    // chu y 1 so truong hop bat buoc trong class, tuy field co trong no
    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toLowerCase()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setUsername(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail() != null ? oAuth2UserInfo.getEmail() : "");
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        user.setEnabled(true);
        user.setRole(Role.USER);
        
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setUsername(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }
}
