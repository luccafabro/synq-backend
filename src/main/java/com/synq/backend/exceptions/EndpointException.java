package com.synq.backend.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


public class EndpointException extends RuntimeException {
    @Getter
    private final HttpStatus status;
    
    public EndpointException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
