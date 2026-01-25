package br.com.gorillaroxo.sanjy.client.web.controller.dto.request;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MealRecordControllerRequestDto(
        @NotNull
        @Schema(
                description = "ID of the meal type (breakfast, lunch, snack, dinner, etc...)",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("ID of the meal type (breakfast, lunch, snack, dinner, etc...). Example: 1")
        Long mealTypeId,

        @NotNull
        @Schema(
                description = """
                    Date and time when the item was consumed at specific timezone. \
                    This field should only be set when registering a meal that was eaten in the past and forgotten to be logged at the time. \
                    Must be a past or present date/time (cannot be in the future). If not provided, defaults to current time.
                    """,
                example = RequestConstants.Examples.DATE_TIME_TIMEZONE,
                format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT_TIMEZONE,
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("""
                    Date and time when the item was consumed at specific timezone. \
                    This field should only be set when registering a meal that was eaten in the past and forgotten to be logged at the time. \
                    Must be a past or present date/time (cannot be in the future). If not provided, defaults to current time.
                    Example: \
                    """ + RequestConstants.Examples.DATE_TIME)
        ZonedDateTime consumedAt,

        @NotNull
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
                    ID of the chosen diet plan option. Required when isFreeMeal = FALSE, should be NULL when isFreeMeal = TRUE
                    """, example = "5", nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("""
            ID of the chosen diet plan option. Required when isFreeMeal = FALSE, should be NULL when isFreeMeal = TRUE
            """)
        Long standardOptionId,

        @Schema(
                description = """
                    Text description of the free meal item. Required when isFreeMeal = TRUE, should be NULL when isFreeMeal = FALSE
                    """,
                example = "Grilled chicken with vegetables",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("""
            Text description of the free meal item. Required when isFreeMeal = TRUE, should be NULL when isFreeMeal = FALSE. Example: Grilled chicken with vegetables
            """)
        String freeMealDescription,

        @NotNull
        @Schema(
                description = "Quantity of the item consumed. Defaults to 1.0 if not provided",
                example = "1",
                defaultValue = "1",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("Quantity of the item consumed. Defaults to 1.0 if not provided. Example: 1")
        Double quantity,

        @NotBlank
        @Schema(
                description = """
                    Unit of measurement for the quantity (serving, g, ml, units, etc...). Defaults to 'serving' if not provided
                    """,
                example = "serving",
                defaultValue = "serving",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("""
            Unit of measurement for the quantity (serving, g, ml, units, etc...). Defaults to 'serving' if not provided
            """)
        String unit,

        @Schema(
                description = "Optional field for additional observations or notes about the meal",
                example = "Extra spicy, no salt",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription(
                "Optional field for additional observations or notes about the meal. Example: Extra spicy, no salt")
        String notes) {}
