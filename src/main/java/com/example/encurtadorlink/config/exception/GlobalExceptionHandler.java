package com.example.encurtadorlink.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO: Fazer um método para lidar com exceptions genéricas

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDTOInfo(MethodArgumentNotValidException e){

        // TODO: Mudar posteriormente para um logger
        System.out.println("Request validation exception: " + e.getMessage());
        System.out.println("Exception path: " + e);

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(
                error -> {
                    String name = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    errors.put(name, message);
                }
        );

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                errors.toString()
        );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExist(UserAlreadyExistsException e){
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        String message = e.getMessage();

        System.out.println("User not available exception: " + message);
        System.out.println("Exception path: " + e);

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message
        );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}
