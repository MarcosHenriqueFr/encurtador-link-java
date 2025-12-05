package com.example.encurtadorlink.fixtures;

import com.example.encurtadorlink.model.Link;

import java.time.LocalDateTime;

public class LinkFixture {

    public static Link createLinkFix(){
        return Link.builder()
                .id(1L)
                .user(null)
                .log(null)
                .active(true)
                .creationDate(LocalDateTime.now())
                .qtClicks(0)
                .shortCode("1111")
                .originalUrl("teste.com")
                .build();
    }
}
