package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MetadataResponseDto;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Builder;

@Builder
public record StandardOptionSimplifiedControllerResponseDto(
        @JsonPropertyDescription("Unique identifier of the Standard Option. Example: 12")
        Long id,

        @JsonPropertyDescription("Option number within the meal type (1, 2, 3, etc). Example: 2")
        Long optionNumber,

        @JsonPropertyDescription("""
            Complete description of foods that compose this meal option. Example: 2 slices of whole grain bread + 2 scrambled eggs + 1 banana + 200ml of coffee without sugar
            """) String description,

        @JsonPropertyDescription(
                "Metadata information containing creation and last update timestamps, along with other contextual data")
        MetadataResponseDto metadata) {}
