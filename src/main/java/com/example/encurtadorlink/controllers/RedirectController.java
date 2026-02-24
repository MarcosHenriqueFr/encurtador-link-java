package com.example.encurtadorlink.controllers;

import com.example.encurtadorlink.dto.AccessContextDTO;
import com.example.encurtadorlink.services.LinkService;
import com.example.encurtadorlink.services.RequestMetadataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RedirectController {

    private final LinkService linkService;
    private final RequestMetadataExtractor metadataExtractor;

    public RedirectController(LinkService linkService, RequestMetadataExtractor metadataExtractor){
        this.linkService = linkService;
        this.metadataExtractor = metadataExtractor;
    }

    @GetMapping(path = "{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request){

        AccessContextDTO contextDTO = new AccessContextDTO(
                metadataExtractor.extractClientIp(request),
                metadataExtractor.extractUserAgent(request),
                metadataExtractor.extractReferrer(request)
        );

        String originalUrl = linkService.resolveShortCode(shortCode, contextDTO);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
