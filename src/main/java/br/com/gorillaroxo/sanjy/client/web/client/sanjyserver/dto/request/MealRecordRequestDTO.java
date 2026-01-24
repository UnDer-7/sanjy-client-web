package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.Instant;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MealRecordRequestDTO(
        @JsonPropertyDescription("ID of the meal type (breakfast, lunch, snack, dinner, etc...). Example: 1")
        Long mealTypeId,

        @JsonPropertyDescription("""
                    Date and time when the item was consumed in UTC timezone (ISO 8601 format). \
                    This field should only be set when registering a meal that was eaten in the past and forgotten to be logged at the time. \
                    Must be a past or present date/time (cannot be in the future). If not provided, defaults to current time.
                    Example: \
                    """ + RequestConstants.Examples.DATE_TIME)
        Instant consumedAt,

        @JsonPropertyDescription("""
        Indicates if this is a free meal (off-plan) or a standard meal (following the diet plan).
        TRUE = free meal | FALSE = standard meal
        """) Boolean isFreeMeal,

        @JsonPropertyDescription(
                "ID of the chosen diet plan option. Required when isFreeMeal = FALSE, should be NULL when isFreeMeal = TRUE")
        Long standardOptionId,

        @JsonPropertyDescription(
                "Text description of the free meal item. Required when isFreeMeal = TRUE, should be NULL when isFreeMeal = FALSE. Example: Grilled chicken with vegetables")
        String freeMealDescription,

        @JsonPropertyDescription("Quantity of the item consumed. Defaults to 1.0 if not provided. Example: 1")
        Double quantity,

        @JsonPropertyDescription(
                "Unit of measurement for the quantity (serving, g, ml, units, etc...). Defaults to 'serving' if not provided")
        String unit,

        @JsonPropertyDescription(
                "Optional field for additional observations or notes about the meal. Example: Extra spicy, no salt")
        String notes) {}
