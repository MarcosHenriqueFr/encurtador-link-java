package com.example.encurtadorlink.config.exception;

public class ShortURLAlreadyExistsException extends RuntimeException {
    public ShortURLAlreadyExistsException(String message) {
        super(message);
    }
}
