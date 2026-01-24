package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record StandardOptionControllerResponseDTO(
        @Schema(
                description = "Unique identifier of the Standard Option",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("Unique identifier of the Standard Option. Example: 12")
        Long id,

        @Schema(
                description = "Option number within the meal type (1, 2, 3, etc)",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("Option number within the meal type (1, 2, 3, etc). Example: 2")
        Long optionNumber,

        @Schema(
                description = "Complete description of foods that compose this meal option",
                example = "2 slices of whole grain bread + 2 scrambled eggs + 1 banana + 200ml of coffee without sugar",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription(
                "Complete description of foods that compose this meal option. Example: 2 slices of whole grain bread + 2 scrambled eggs + 1 banana + 200ml of coffee without sugar")
        String description,

        @Schema(
                description = "Identifier of the meal type this standard option belongs to",
                example = "789",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("Identifier of the meal type this standard option belongs to. Example: 789")
        Long mealTypeId,

        @JsonPropertyDescription(
                "Metadata information containing creation and last update timestamps, along with other contextual data")
        MetadataControllerResponseDto metadata) {}
