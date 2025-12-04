package com.example.encurtadorlink.controllers;

import com.example.encurtadorlink.config.security.auth.AuthenticationService;
import com.example.encurtadorlink.dto.LoginRequest;
import com.example.encurtadorlink.dto.UserCreateDTO;
import com.example.encurtadorlink.dto.UserResponseDTO;
import com.example.encurtadorlink.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "users")
@Validated
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final AuthenticationService authenticationService;

    public AuthenticationController (UserService userService, AuthenticationService authenticationService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(path = "register")
    public ResponseEntity<UserResponseDTO> createUser (@RequestBody @Valid UserCreateDTO dto){
        UserResponseDTO user = userService.createUser(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @PostMapping(path = "login")
    public ResponseEntity<String> authenticate (@RequestBody @Valid LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String token = authenticationService.createJWT(authentication);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(token);

    }
}
