package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record StandardOptionRequestDto(
        @JsonPropertyDescription("Option number within the meal type (1, 2, 3, etc). Example: 1")
        Integer optionNumber,

        @JsonPropertyDescription("""
            Complete description of foods that compose this meal option. Example: 2 slices of whole grain bread + 2 scrambled eggs + 1 banana + 200ml of coffee without sugar
            """) String description) {}
