package com.example.encurtadorlink.controllers;

import com.example.encurtadorlink.dto.LinkCreateDTO;
import com.example.encurtadorlink.dto.LinkResponseDTO;
import com.example.encurtadorlink.services.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService){
        this.linkService = linkService;
    }

    @PostMapping(path = "shorten")
    public ResponseEntity<LinkResponseDTO> shortenLink(@RequestBody LinkCreateDTO dto){
        LinkResponseDTO link = linkService.shortenLink(dto);
        return new ResponseEntity<>(link, HttpStatus.CREATED);
    }
}
