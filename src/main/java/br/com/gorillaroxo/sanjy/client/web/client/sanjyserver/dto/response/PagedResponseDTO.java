package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Builder
public record PagedResponseDTO<T>(
    Integer totalPages,
    Integer currentPage,
    Integer pageSize,
    Long totalItems,
    List<T> content
) {
    public PagedResponseDTO {
        content = Objects.requireNonNullElse(content, Collections.emptyList());
    }
}
