package br.com.gorillaroxo.sanjy.client.web.controller.dto.request;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder(toBuilder = true)
public record DietPlanControllerRequestDto(
        @NotBlank
        @Schema(
                description = "Name/identifier of the diet plan",
                example = "Plan N°02 - Cutting",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 100)
        @JsonPropertyDescription("Name/identifier of the diet plan. Example: Plan N°02 - Cutting")
        String name,

        @Schema(
                description =
                        "Date when this diet plan starts (ISO 8601 format). If not provided, defaults to current date",
                example = RequestConstants.Examples.DATE,
                format = RequestConstants.DateTimeFormats.DATE_FORMAT,
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("Date when this diet plan starts. Example: " + RequestConstants.Examples.DATE)
        LocalDate startDate,

        @Schema(
                description = """
                    Date when this diet plan ends (ISO 8601 format). If not provided, defaults to current date + 2 months. \
                    If provided, must be a future date
                    """,
                example = RequestConstants.Examples.DATE,
                format = RequestConstants.DateTimeFormats.DATE_FORMAT,
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Future
        @JsonPropertyDescription("Date when this diet plan ends. Example: " + RequestConstants.Examples.DATE)
        LocalDate endDate,

        @Schema(
                description = "Target daily calories",
                example = "2266",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("Target daily calories. Example: 2266")
        Integer dailyCalories,

        @Schema(
                description = "Target daily protein in grams",
                example = "186",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("Target daily protein in grams. Example: 186")
        Integer dailyProteinInG,

        @Schema(
                description = "Target daily carbohydrates in grams",
                example = "288",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("Target daily carbohydrates in grams. Example: 288")
        Integer dailyCarbsInG,

        @Schema(
                description = "Target daily fat in grams",
                example = "30",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("Target daily fat in grams. Example: 30")
        Integer dailyFatInG,

        @Schema(
                description = "Main goal of this diet plan",
                example = "Body fat reduction with muscle mass preservation",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription(
                "Main goal of this diet plan. Example: Body fat reduction with muscle mass preservation")
        String goal,

        @Schema(
                description = "Additional notes or observations from the nutritionist",
                example = "Patient has lactose intolerance. Avoid dairy products.",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonPropertyDescription("""
            Additional notes or observations from the nutritionist. Example: Patient has lactose intolerance. Avoid dairy products.
            """)
        String nutritionistNotes,

        @Valid
        @NotNull
        @NotEmpty
        @Schema(
                description = "List of meal types associated with this diet plan",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("List of meal types associated with this diet plan.")
        List<MealTypeControllerRequestDto> mealTypes) {

    public DietPlanControllerRequestDto {
        mealTypes = Objects.requireNonNullElseGet(mealTypes, Collections::emptyList);
    }

    public boolean isEmpty() {
        return isBlankOrNullOrEmpty(name)
                && isBlankOrNullOrEmpty(startDate)
                && isBlankOrNullOrEmpty(endDate)
                && isBlankOrNullOrEmpty(dailyCalories)
                && isBlankOrNullOrEmpty(dailyProteinInG)
                && isBlankOrNullOrEmpty(dailyCarbsInG)
                && isBlankOrNullOrEmpty(dailyFatInG)
                && isBlankOrNullOrEmpty(goal)
                && isBlankOrNullOrEmpty(nutritionistNotes)
                && isBlankOrNullOrEmpty(mealTypes);
    }

    private boolean isBlankOrNullOrEmpty(final Object value) {
        if (value == null) {
            return true;
        }

        return switch (value) {
            case String str -> str.isBlank();
            case Collection<?> col -> col.isEmpty();
            default -> false;
        };
    }
}
