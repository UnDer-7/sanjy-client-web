package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
public record MealRecordResponseDTO(
        @JsonPropertyDescription("Unique identifier of the meal record. Example: 1")
        Long id,

        @JsonPropertyDescription("Exact date and time when the item was consumed in UTC timezone (ISO 8601 format). Example: " + RequestConstants.Examples.DATE_TIME)
        Instant consumedAt,

        @JsonPropertyDescription("Meal type information (breakfast, lunch, snack, dinner, etc...). Returns only the ID of the related meal type entity")
        IdOnlyResponseDto mealType,

        @JsonPropertyDescription("""
            Indicates if this is a free meal (off-plan) or a standard meal (following the diet plan).
            TRUE = free meal | FALSE = standard meal
            """)
        Boolean isFreeMeal,

        @JsonPropertyDescription("""
                    The selected diet plan option that was consumed. This field contains the standard option chosen from the diet plan. \
                    Returns only the ID of the related standard option entity. \
                    NULL when isFreeMeal = TRUE (free meals don't follow the plan)
                    """)
        IdOnlyResponseDto standardOption,

        @JsonPropertyDescription("""
            Text description of the free meal item consumed.
            NULL when isFreeMeal = FALSE (standard meals use standardOption instead).
            Example: Grilled chicken with vegetables
            """)
        String freeMealDescription,

        @JsonPropertyDescription("Quantity of the item consumed. Example: 4")
        Double quantity,

        @JsonPropertyDescription("Unit of measurement for the quantity (serving, g, ml, units, etc.). Example: serving")
        String unit,

        @JsonPropertyDescription("Additional observations or notes about the meal. Example: Extra spicy, no salt")
        String notes,

        @JsonPropertyDescription("Metadata information containing creation and last update timestamps, along with other contextual data")
        MetadataResponseDto metadata

) {
}
