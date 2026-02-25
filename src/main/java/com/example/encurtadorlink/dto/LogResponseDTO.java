package com.example.encurtadorlink.dto;

import java.time.LocalDateTime;

public record LogResponseDTO(
       Long id,
       LocalDateTime accessDate,
       String userAgent,
       String referrer
) { }
