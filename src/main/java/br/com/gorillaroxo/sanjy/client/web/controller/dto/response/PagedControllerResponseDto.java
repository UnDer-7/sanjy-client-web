package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record PagedControllerResponseDto<T>(
        @JsonPropertyDescription("Total number of pages available. Example: 5")
        Integer totalPages,

        @JsonPropertyDescription("Current page number (zero-based). Example: 3")
        Integer currentPage,

        @JsonPropertyDescription("Number of items per page. Example: 20")
        Integer pageSize,

        @JsonPropertyDescription("Total number of items across all pages. Example: 100")
        Long totalItems,

        @JsonPropertyDescription("List of items in the current page")
        List<T> content) {
    public PagedControllerResponseDto {
        content = Objects.requireNonNullElse(content, Collections.emptyList());
    }
}
