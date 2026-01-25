package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Builder
public record MealRecordControllerResponseDto(
        @Schema(
                description = "Unique identifier of the meal record",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("Unique identifier of the meal record. Example: 1")
        Long id,

        @Schema(
                description = "Exact date and time when the item was consumed in UTC timezone (ISO 8601 format)",
                example = RequestConstants.Examples.DATE_TIME,
                format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT_UTC,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription(
                "Exact date and time when the item was consumed in UTC timezone (ISO 8601 format). Example: "
                        + RequestConstants.Examples.DATE_TIME)
        Instant consumedAt,

        @Schema(
                description = """
                    Meal type information (breakfast, lunch, snack, dinner, etc...). Returns only the ID of the related meal type entity
                    """,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("""
            Meal type information (breakfast, lunch, snack, dinner, etc...). Returns only the ID of the related meal type entity
            """)
        MealTypeSimplifiedControllerResponseDto mealType,

        @Schema(
                description = """
                    Indicates if this is a free meal (off-plan) or a standard meal (following the diet plan). TRUE = free meal | FALSE = standard meal
                    """,
                example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("""
            Indicates if this is a free meal (off-plan) or a standard meal (following the diet plan).
            TRUE = free meal | FALSE = standard meal
            """)
        Boolean isFreeMeal,

        @Schema(description = """
                    The selected diet plan option that was consumed. This field contains the standard option chosen from the diet plan. \
                    Returns only the ID of the related standard option entity. \
                    NULL when isFreeMeal = TRUE (free meals don't follow the plan)
                    """, nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("""
                    The selected diet plan option that was consumed. This field contains the standard option chosen from the diet plan. \
                    Returns only the ID of the related standard option entity. \
                    NULL when isFreeMeal = TRUE (free meals don't follow the plan)
                    """)
        StandardOptionSimplifiedControllerResponseDto standardOption,

        @Schema(
                description = "Text description of the free meal item consumed. "
                        + "NULL when isFreeMeal = FALSE (standard meals use standardOption instead)",
                example = "Grilled chicken with vegetables",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("""
            Text description of the free meal item consumed.
            NULL when isFreeMeal = FALSE (standard meals use standardOption instead).
            Example: Grilled chicken with vegetables
            """)
        String freeMealDescription,

        @Schema(
                description = "Quantity of the item consumed",
                example = "1.5",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("Quantity of the item consumed. Example: 4")
        Double quantity,

        @Schema(
                description = "Unit of measurement for the quantity (serving, g, ml, units, etc.)",
                example = "serving",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription(
                "Unit of measurement for the quantity (serving, g, ml, units, etc.). Example: serving")
        String unit,

        @Schema(
                description = "Additional observations or notes about the meal",
                example = "Extra spicy, no salt",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true)
        @JsonPropertyDescription("Additional observations or notes about the meal. Example: Extra spicy, no salt")
        String notes,

        @Schema(description = """
                    Metadata information containing creation and last update timestamps, along with other contextual data
                    """, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription(
                "Metadata information containing creation and last update timestamps, along with other contextual data")
        MetadataControllerResponseDto metadata) {}
