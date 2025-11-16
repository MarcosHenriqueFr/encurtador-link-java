package com.example.encurtadorlink.config.security.auth;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JwtService jwtService;

    public AuthenticationService (JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String createJWT(Authentication authentication){
        return jwtService.generateToken(authentication);
    }
}
