package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Builder;

@Builder
public record IdOnlyResponseDto(
        @JsonPropertyDescription("Unique identifier of the entity. Example: 123")
        Long id) {}
