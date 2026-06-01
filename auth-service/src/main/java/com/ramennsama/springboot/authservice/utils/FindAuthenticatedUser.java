package com.ramennsama.springboot.authservice.utils;


import com.ramennsama.springboot.authservice.entity.User;

public interface FindAuthenticatedUser {
    User getAuthenticatedUser();
    
    default Long getUserId() {
        return getAuthenticatedUser().getId();
    }
}
