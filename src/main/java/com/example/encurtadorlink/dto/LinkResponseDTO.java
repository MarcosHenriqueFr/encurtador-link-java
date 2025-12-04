package com.example.encurtadorlink.dto;

import java.time.LocalDateTime;

public record LinkResponseDTO (
    Long id,
    String originalUrl,
    String shortCode,
    UserResponseDTO user,
    LocalDateTime creationDate
) { }
