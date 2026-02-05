package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record IdOnlyControllerResponseDto(
    @Schema(
        description = "Unique identifier of the entity",
        example = "123",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonPropertyDescription("Unique identifier of the entity. Example: 123")
    Long id) {
}
