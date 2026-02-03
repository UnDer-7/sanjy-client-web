package br.com.gorillaroxo.sanjy.client.web.test.builder;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.DietPlanControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealTypeControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.StandardOptionControllerRequestDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class DtoControllerBuilders {

    private DtoControllerBuilders() {
        throw new IllegalStateException("Utility class");
    }

    public static DietPlanControllerRequestDto.DietPlanControllerRequestDtoBuilder buildDietPlanControllerRequestDto() {
        return DietPlanControllerRequestDto.builder()
            .name("Plan N°02 - Cutting")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(2))
            .dailyCalories(2266)
            .dailyProteinInG(186)
            .dailyCarbsInG(288)
            .dailyFatInG(30)
            .goal("Main goal of this diet plan. Example: Body fat reduction with muscle mass preservation")
            .nutritionistNotes("Additional notes or observations from the nutritionist. Example: Patient has lactose intolerance. Avoid dairy products.")
            .mealTypes(List.of(buildMealTypeControllerRequestDto().build()));
    }

    public static MealTypeControllerRequestDto.MealTypeControllerRequestDtoBuilder buildMealTypeControllerRequestDto() {
        return MealTypeControllerRequestDto.builder()
            .name("Breakfast")
            .scheduledTime(LocalTime.now())
            .observation("30 g proteína | 20 g carbo | 5 g gordura | 250 kcal")
            .standardOptions(List.of(buildStandardOptionControllerRequestDto().build()));
    }

    public static StandardOptionControllerRequestDto.StandardOptionControllerRequestDtoBuilder buildStandardOptionControllerRequestDto() {
        return StandardOptionControllerRequestDto.builder()
            .optionNumber(1)
            .description("2 slices of whole grain bread + 2 scrambled eggs + 1 banana + 200ml of coffee without sugar");
    }
}
