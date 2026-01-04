package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MetadataResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.StandardOptionResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Builder
public record MealTypeControllerResponseDTO(
    @Schema(
        description = "Unique identifier of the meal type",
        example = "123",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonPropertyDescription("Unique identifier of the meal type. Example: 2266")
    Long id,

    @Schema(
        description = "Meal type name",
        example = "Breakfast",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50)
    @JsonPropertyDescription("Meal type name. Example: Breakfast")
    String name,

    @Schema(
        description = "Scheduled time for this meal",
        example = RequestConstants.Examples.TIME,
        type = "string",
        pattern = "^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonPropertyDescription("Scheduled time for this meal. Example: " + RequestConstants.Examples.TIME)
    LocalTime scheduledTime,

    @Schema(
        description = """
                    Additional observations about the meal type, such as target macronutrients (protein, carbs, fat in grams) and total calories (kcal)
                    """,
        example = "30 g proteína | 20 g carbo | 5 g gordura | 250 kcal",
        nullable = true,
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonPropertyDescription("Additional observations about the meal type, such as target macronutrients (protein, carbs, fat in grams) and total calories (kcal). Example: 30 g proteína | 20 g carbo | 5 g gordura | 250 kcal")
    String observation,

    @Schema(
        description = "Identifier of the diet plan this meal type belongs to",
        example = "456",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonPropertyDescription("Identifier of the diet plan this meal type belongs to. Example: 30")
    Long dietPlanId,

    @Schema(
        description = "Set of standard food options for this meal type",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonPropertyDescription("Set of standard food options for this meal type")
    List<StandardOptionControllerResponseDTO> standardOptions,

    @Schema(description = """
                    Metadata information containing creation and last update timestamps, along with other contextual data
                    """, requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonPropertyDescription("Metadata information containing creation and last update timestamps, along with other contextual data")
    MetadataControllerResponseDto metadata
) {

    public MealTypeControllerResponseDTO {
        standardOptions = Objects.requireNonNullElse(standardOptions, Collections.emptyList());
    }
}
