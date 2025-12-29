package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import lombok.Builder;

@Builder
public record StandardOptionResponseDTO(
    Long id,
    Long optionNumber,
    String description,
    Long mealTypeId
) {

}
