package com.ramennsama.springboot.apigateway.service;

import io.jsonwebtoken.Claims;
import java.util.Date;

public interface JwtService {

    Claims extractAllClaims(String token);

    String extractUsername(String token);

    Date extractExpiration(String token);

    boolean validateToken(String token, String username);
}
