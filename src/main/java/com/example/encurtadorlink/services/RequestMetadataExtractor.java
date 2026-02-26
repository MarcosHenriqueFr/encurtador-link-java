package com.example.encurtadorlink.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestMetadataExtractor {

    /**
     * <p>
     *     Essa função pega as informações do ip mesmo com o redirecionamento do traefik
     *     e do container Docker.
     * </p>
     * @param request
     * @return o ip de quem clicou no link curto
     */
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
