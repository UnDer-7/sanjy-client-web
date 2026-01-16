package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record MealTypeSimplifiedResponseDto(
    @JsonPropertyDescription("Unique identifier of the meal type. Example: 2266")
    Long id,

    @JsonPropertyDescription("Meal type name. Example: Breakfast")
    String name,

    @JsonPropertyDescription("Scheduled time for this meal. Example: " + RequestConstants.Examples.TIME)
    LocalTime scheduledTime,

    @JsonPropertyDescription("Additional observations about the meal type, such as target macronutrients (protein, carbs, fat in grams) and total calories (kcal). Example: 30 g proteína | 20 g carbo | 5 g gordura | 250 kcal")
    String observation,

    @JsonPropertyDescription("Metadata information containing creation and last update timestamps, along with other contextual data")
    MetadataResponseDto metadata
) {

}
