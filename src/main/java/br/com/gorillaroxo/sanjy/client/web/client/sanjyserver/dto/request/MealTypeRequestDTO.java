package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record MealTypeRequestDTO(
        @JsonPropertyDescription("Meal type name. Example: Breakfast")
        String name,

        @JsonPropertyDescription("Scheduled time for this meal. Example: " + RequestConstants.Examples.TIME)
        LocalTime scheduledTime,

        @JsonPropertyDescription(
                "Additional observations about the meal type, such as target macronutrients (protein, carbs, fat in grams) and total calories (kcal). Example: 30 g proteína | 20 g carbo | 5 g gordura | 250 kcal")
        String observation,

        @JsonPropertyDescription("Set of standard food options for this meal type")
        List<StandardOptionRequestDTO> standardOptions) {

    public MealTypeRequestDTO {
        standardOptions = Objects.requireNonNullElseGet(standardOptions, Collections::emptyList);
    }
}
