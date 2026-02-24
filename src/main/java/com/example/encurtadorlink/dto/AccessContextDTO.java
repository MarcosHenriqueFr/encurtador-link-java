package com.example.encurtadorlink.dto;

public record AccessContextDTO(
        String clientIp,
        String userAgent,
        String referrer
) { }
