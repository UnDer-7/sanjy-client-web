package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record MealTypeResponseDto(
        @JsonPropertyDescription("Unique identifier of the meal type. Example: 2266")
        Long id,

        @JsonPropertyDescription("Meal type name. Example: Breakfast")
        String name,

        @JsonPropertyDescription("Scheduled time for this meal. Example: " + RequestConstants.Examples.TIME)
        LocalTime scheduledTime,

        @JsonPropertyDescription("""
            Additional observations about the meal type, such as target macronutrients (protein, carbs, fat in grams) and total calories (kcal). \
            Example: 30 g proteína | 20 g carbo | 5 g gordura | 250 kcal
            """)
        String observation,

        @JsonPropertyDescription("Identifier of the diet plan this meal type belongs to. Example: 30")
        Long dietPlanId,

        @JsonPropertyDescription("Set of standard food options for this meal type")
        List<StandardOptionResponseDto> standardOptions,

        @JsonPropertyDescription(
                "Metadata information containing creation and last update timestamps, along with other contextual data")
        MetadataResponseDto metadata) {

    public MealTypeResponseDto {
        standardOptions = Objects.requireNonNullElse(standardOptions, Collections.emptyList());
    }
}
