package com.example.encurtadorlink.fixtures;

import com.example.encurtadorlink.model.LogAccess;

import java.time.LocalDateTime;

public class LogAccessFixture {

    public static LogAccess createLogFix(){
        return LogAccess.builder()
                .id(1L)
                .link(null)
                .accessDate(LocalDateTime.now())
                .userIp("127.0.0.1")
                .userAgent("browser")
                .referrer("ref")
                .build();
    }
}
