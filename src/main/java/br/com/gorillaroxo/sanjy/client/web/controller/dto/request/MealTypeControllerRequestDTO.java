package br.com.gorillaroxo.sanjy.client.web.controller.dto.request;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.StandardOptionRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record MealTypeControllerRequestDTO(

    @NotBlank
    @Schema(
        description = "Meal type name. Must be unique within the diet plan's meal types list "
            + "(case-insensitive comparison, trimmed of leading/trailing whitespace)",
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

    @Valid
    @NotNull
    @NotEmpty
    @Schema(
        description = "List of standard food options for this meal type",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonPropertyDescription("Set of standard food options for this meal type")
    List<StandardOptionControllerRequestDTO> standardOptions
) {

    public MealTypeControllerRequestDTO {
        standardOptions = Objects.requireNonNullElseGet(standardOptions, Collections::emptyList);
    }

}
