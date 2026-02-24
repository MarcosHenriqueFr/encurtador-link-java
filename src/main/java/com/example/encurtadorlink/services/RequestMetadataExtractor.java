package com.example.encurtadorlink.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestMetadataExtractor {

    public String extractClientIp(HttpServletRequest request){
        String forwarded = request.getHeader("X-Forwarded-For");

        // Caso tenha as informações necessárias
        if (forwarded != null && !forwarded.isEmpty()){
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    public String extractUserAgent(HttpServletRequest request){
        return request.getHeader("User-Agent");
    }

    public String extractReferrer(HttpServletRequest request){
        return request.getHeader("Referer");
    }
}
