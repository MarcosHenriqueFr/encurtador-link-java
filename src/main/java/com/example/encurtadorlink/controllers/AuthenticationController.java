package com.example.encurtadorlink.controllers;

import com.example.encurtadorlink.dto.UserCreateDTO;
import com.example.encurtadorlink.dto.UserResponseDTO;
import com.example.encurtadorlink.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "users")
@Validated
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController (UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "register")
    public ResponseEntity<UserResponseDTO> createUser (@RequestBody @Valid UserCreateDTO dto){
        UserResponseDTO user = userService.createUser(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }
}
