package com.synq.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationDTO {
    int currentPage;
    int pageSize;
    int totalPages;
    boolean isLastPage;
}
