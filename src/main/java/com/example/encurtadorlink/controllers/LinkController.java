package com.example.encurtadorlink.controllers;

import com.example.encurtadorlink.dto.LinkCreateDTO;
import com.example.encurtadorlink.dto.LinkResponseDTO;
import com.example.encurtadorlink.dto.LogResponseDTO;
import com.example.encurtadorlink.services.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService){
        this.linkService = linkService;
    }

    /**
     * <p>
     *     Essa requisição trata tanto de usuários que tem conta no sistema, quanto os que não tem.
     *     Ela analisa se a requisição JWT chegou e adiciona um email se isso for verdade,
     *     caso não seja, o email nulo é mandado para a camada de service.
     * </p>
     * */
    @PostMapping("/shorten")
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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(link);
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

    @GetMapping("/info/{shortCode}")
    public ResponseEntity<List<LogResponseDTO>> getShortLinkInfo(@PathVariable String shortCode, @AuthenticationPrincipal Jwt jwt){
        List<LogResponseDTO> info = linkService.getLinkInformation(jwt.getSubject(), shortCode);

        return ResponseEntity
                .ok(info);
    }
}

