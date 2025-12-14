package com.example.encurtadorlink.controllers;

import com.example.encurtadorlink.dto.LinkCreateDTO;
import com.example.encurtadorlink.dto.LinkResponseDTO;
import com.example.encurtadorlink.services.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService){
        this.linkService = linkService;
    }

    // TODO: Resolver o problema de autorização
    @PostMapping(path = "shorten")
    public ResponseEntity<LinkResponseDTO> shortenLink(@RequestBody LinkCreateDTO dto, Authentication authentication){
        Jwt jwt = null;
        if(authentication != null && authentication.getPrincipal() instanceof Jwt jwtPrincipal){
            jwt = jwtPrincipal;
        }

        String email = null;
        if(jwt != null){
            email = jwt.getSubject();
        }

        LinkResponseDTO link = linkService.shortenLink(dto, email);
        return new ResponseEntity<>(link, HttpStatus.CREATED);
    }

    @GetMapping(path = "links")
    public ResponseEntity<List<LinkResponseDTO>> showLinkPerUser(@AuthenticationPrincipal Jwt jwt){
        List<LinkResponseDTO> links = linkService.showLinksPerUser(jwt.getSubject());
        return ResponseEntity
                .ok(links);
    }

    @DeleteMapping(path = "links/{shortCode}")
    public ResponseEntity<Void> deleteShortLink(@PathVariable String shortCode, @AuthenticationPrincipal Jwt jwt){
        linkService.deleteShortLink(jwt.getSubject(), shortCode);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}

