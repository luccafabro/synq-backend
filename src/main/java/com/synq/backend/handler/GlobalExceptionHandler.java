package com.synq.backend.handler;


import com.synq.backend.dto.response.ResponseDTO;
import com.synq.backend.exceptions.EndpointException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice()
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<String>> handleAllExceptions(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseDTO.of(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, true);
    }

    @ExceptionHandler(EndpointException.class)
    public ResponseEntity<ResponseDTO<String>> handleNotFound(EndpointException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseDTO.of(ex.getMessage(), ex.getStatus(), true);
    }

}
