package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record IdOnlyResponseDto(
    @JsonPropertyDescription("Unique identifier of the referenced entity. Example: 12")
    Long id
) {

}
