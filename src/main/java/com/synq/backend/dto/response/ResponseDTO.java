package com.synq.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseDTO<T> {
    private T data;
    private String message;
    private String status;
    private boolean error;
    private PaginationDTO pagination;

    public static <T> ResponseEntity<ResponseDTO<T>> of(T data, HttpStatus status, boolean error) {
        var response = ResponseDTO.<T>builder()
                .data(data)
                .message(status.getReasonPhrase())
                .status(status.toString())
                .error(error)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ResponseDTO<T>> ofPagination(Page<T> data, HttpStatus status) {
        var pagination = new PaginationDTO(data.getNumber(), data.getSize(), data.getTotalPages(), data.isLast());
        var response = ResponseDTO.<T>builder()
                .data((T) data.getContent())
                .message(status.getReasonPhrase())
                .status(status.toString())
                .error(false)
                .pagination(pagination)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ResponseDTO<T>> of(T data, HttpStatus status) {
        var response = ResponseDTO.<T>builder()
                .data(data)
                .message(status.getReasonPhrase())
                .status(status.toString())
                .error(false)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ResponseDTO<T>> of(T data, HttpStatus status, HttpHeaders headers) {
        var response = ResponseDTO.<T>builder()
                .data(data)
                .message(status.getReasonPhrase())
                .status(status.toString())
                .error(false)
                .build();
        return ResponseEntity.status(status).headers(headers).body(response);
    }
}
