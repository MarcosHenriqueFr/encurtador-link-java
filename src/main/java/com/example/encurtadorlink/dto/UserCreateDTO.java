package com.example.encurtadorlink.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateDTO(

        @NotEmpty
        @Size(min = 3, message = "Name must have at least 3 letters.")
        String name,

        @NotEmpty
        @Email(
                regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "Email must be valid."
        )
        String email,

        @NotEmpty
        @Pattern(
                message = "Password must have at least 8 characters. " +
                        "1 uppercase letter, 1 lowercase letter, 1 number(0/9) and 1 special character",
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$"
        )
        String password
) { }
