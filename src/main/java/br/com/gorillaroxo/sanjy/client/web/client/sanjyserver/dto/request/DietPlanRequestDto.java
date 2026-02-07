package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder(toBuilder = true)
public record DietPlanRequestDto(
        @JsonPropertyDescription("Name/identifier of the diet plan. Example: Plan N°02 - Cutting")
        String name,

        @JsonPropertyDescription("Date when this diet plan starts. Example: " + RequestConstants.Examples.DATE)
        LocalDate startDate,

        @JsonPropertyDescription("Date when this diet plan ends. Example: " + RequestConstants.Examples.DATE)
        LocalDate endDate,

        @JsonPropertyDescription("Target daily calories. Example: 2266")
        Integer dailyCalories,

        @JsonPropertyDescription("Target daily protein in grams. Example: 186")
        Integer dailyProteinInG,

        @JsonPropertyDescription("Target daily carbohydrates in grams. Example: 288")
        Integer dailyCarbsInG,

        @JsonPropertyDescription("Target daily fat in grams. Example: 30")
        Integer dailyFatInG,

        @JsonPropertyDescription(
                "Main goal of this diet plan. Example: Body fat reduction with muscle mass preservation")
        String goal,

        @JsonPropertyDescription("""
            Additional notes or observations from the nutritionist. Example: Patient has lactose intolerance. Avoid dairy products.
            """) String nutritionistNotes,

        @JsonPropertyDescription("List of meal types associated with this diet plan.")
        List<MealTypeRequestDto> mealTypes) {

    public DietPlanRequestDto {
        mealTypes = Objects.requireNonNullElseGet(mealTypes, Collections::emptyList);
    }
}
