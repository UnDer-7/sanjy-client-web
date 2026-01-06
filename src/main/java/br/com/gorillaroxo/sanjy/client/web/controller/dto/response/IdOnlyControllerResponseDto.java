package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record IdOnlyControllerResponseDto(
    @JsonPropertyDescription("Unique identifier of the referenced entity. Example: 12")
    Long id
) {

}
