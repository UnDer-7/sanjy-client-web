package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import lombok.Builder;

@Builder(toBuilder = true)
public record DietPlanControllerResponseDto(
        @Schema(
                description = "Unique identifier of the Diet Plan",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("Unique identifier of the Diet Plan. Example: 123")
        Long id,

        @Schema(
                description = "Name/identifier of the diet plan",
                example = "Plan N°02 - Cutting",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 100)
        @JsonPropertyDescription("Name/identifier of the diet plan. Example: Plan N°02 - Cutting")
        String name,

        @Schema(
                description = "Date when this diet plan starts",
                example = RequestConstants.Examples.DATE,
                format = RequestConstants.DateTimeFormats.DATE_FORMAT,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("Date when this diet plan starts. Example: " + RequestConstants.Examples.DATE)
        LocalDate startDate,

        @Schema(
                description = "Date when this diet plan ends",
                example = RequestConstants.Examples.DATE,
                format = RequestConstants.DateTimeFormats.DATE_FORMAT,
                requiredMode = Schema.RequiredMode.REQUIRED)
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
            Additional notes or observations from the nutritionist. Example: Patient has lactose intolerance. Avoid dairy products
            """)
        String nutritionistNotes,

        @Schema(
                description = "List of meal types associated with this diet plan",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("List of meal types associated with this diet plan")
        Set<MealTypeControllerResponseDto> mealTypes,

        @Schema(
                description = "Indicates whether this diet plan is currently active",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription("Indicates whether this diet plan is currently active. Example: true")
        Boolean isActive,

        @Schema(description = """
                    Metadata information containing creation and last update timestamps, along with other contextual data
                    """, requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonPropertyDescription(
                "Metadata information containing creation and last update timestamps, along with other contextual data")
        MetadataControllerResponseDto metadata) {

    public DietPlanControllerResponseDto {
        mealTypes = Objects.requireNonNullElse(mealTypes, Collections.emptySet());
    }
}
