package com.example.encurtadorlink.config.exception;

public class ShortURLNotFoundException extends RuntimeException {
    public ShortURLNotFoundException(String message) {
        super(message);
    }
}
