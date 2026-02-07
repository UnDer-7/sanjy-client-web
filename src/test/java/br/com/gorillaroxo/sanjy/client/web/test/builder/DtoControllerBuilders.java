package br.com.gorillaroxo.sanjy.client.web.test.builder;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.DietPlanControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealRecordControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealTypeControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.StandardOptionControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.IdOnlyControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordCreatedControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MetadataControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ProjectInfoMaintenanceControllerResponseDto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public final class DtoControllerBuilders {

    private DtoControllerBuilders() {
        throw new IllegalStateException("Utility class");
    }

    public static MealRecordCreatedControllerResponseDto.MealRecordCreatedControllerResponseDtoBuilder
            buildMealRecordCreatedControllerResponseDtoFreeMeal() {
        return buildMealRecordCreatedControllerResponseDto()
                .isFreeMeal(true)
                .standardOption(null)
                .freeMealDescription("BigMac");
    }

    public static MealRecordCreatedControllerResponseDto.MealRecordCreatedControllerResponseDtoBuilder
            buildMealRecordCreatedControllerResponseDtoPlannedMeal() {
        return buildMealRecordCreatedControllerResponseDto()
                .isFreeMeal(false)
                .standardOption(buildIdOnlyControllerResponseDto()
                        .id(DtoBuilders.STANDARD_OPTION_ID)
                        .build())
                .freeMealDescription(null);
    }

    public static MealRecordCreatedControllerResponseDto.MealRecordCreatedControllerResponseDtoBuilder
            buildMealRecordCreatedControllerResponseDto() {
        return MealRecordCreatedControllerResponseDto.builder()
                .id(DtoBuilders.MEAL_RECORD_ID)
                .consumedAt(Instant.now())
                .mealType(buildIdOnlyControllerResponseDto()
                        .id(DtoBuilders.MEAL_TYPE_ID)
                        .build())
                .isFreeMeal(true)
                .standardOption(null)
                .freeMealDescription("pacote de biscoito")
                .quantity(1.2)
                .unit("serving")
                .notes(null)
                .metadata(buildMetadataControllerResponseDto().build());
    }

    public static IdOnlyControllerResponseDto.IdOnlyControllerResponseDtoBuilder buildIdOnlyControllerResponseDto() {
        return IdOnlyControllerResponseDto.builder().id(2L);
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
                .nutritionistNotes(
                        "Additional notes or observations from the nutritionist. Example: Patient has lactose intolerance. Avoid dairy products.")
                .mealTypes(List.of(buildMealTypeControllerRequestDto().build()));
    }

    public static MealTypeControllerRequestDto.MealTypeControllerRequestDtoBuilder buildMealTypeControllerRequestDto() {
        return MealTypeControllerRequestDto.builder()
                .name("Breakfast")
                .scheduledTime(LocalTime.now())
                .observation("30 g proteína | 20 g carbo | 5 g gordura | 250 kcal")
                .standardOptions(
                        List.of(buildStandardOptionControllerRequestDto().build()));
    }

    public static StandardOptionControllerRequestDto.StandardOptionControllerRequestDtoBuilder
            buildStandardOptionControllerRequestDto() {
        return StandardOptionControllerRequestDto.builder()
                .optionNumber(1)
                .description(
                        "2 slices of whole grain bread + 2 scrambled eggs + 1 banana + 200ml of coffee without sugar");
    }

    public static MetadataControllerResponseDto.MetadataControllerResponseDtoBuilder
            buildMetadataControllerResponseDto() {
        return MetadataControllerResponseDto.builder().createdAt(Instant.now()).updatedAt(Instant.now());
    }

    public static MealRecordControllerRequestDto.MealRecordControllerRequestDtoBuilder
            buildMealRecordControllerRequestDto() {
        return MealRecordControllerRequestDto.builder()
                .mealTypeId(DtoBuilders.MEAL_TYPE_ID)
                .consumedAt(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .isFreeMeal(true)
                .standardOptionId(null)
                .freeMealDescription("pacote de biscoito")
                .quantity(1.0)
                .unit("serving")
                .notes(null);
    }

    public static MealRecordControllerRequestDto.MealRecordControllerRequestDtoBuilder
            buildMealRecordControllerRequestDtoFreeMeal() {
        return buildMealRecordControllerRequestDto()
                .isFreeMeal(true)
                .standardOptionId(null)
                .freeMealDescription("pacote de biscoito");
    }

    public static MealRecordControllerRequestDto.MealRecordControllerRequestDtoBuilder
            buildMealRecordControllerRequestDtoPlannedMeal() {
        return buildMealRecordControllerRequestDto()
                .isFreeMeal(false)
                .standardOptionId(DtoBuilders.STANDARD_OPTION_ID)
                .freeMealDescription(null);
    }

    public static ProjectInfoMaintenanceControllerResponseDto.ProjectInfoMaintenanceControllerResponseDtoBuilder buildProjectInfoMaintenanceControllerResponseDto() {
        return ProjectInfoMaintenanceControllerResponseDto.builder()
            .sanjyClientWeb(buildProjectInfoMaintenanceControllerProjectResponseDto().build())
            .sanjyServer(buildProjectInfoMaintenanceControllerProjectResponseDto().build());
    }

    public static ProjectInfoMaintenanceControllerResponseDto.Project.ProjectBuilder buildProjectInfoMaintenanceControllerProjectResponseDto() {
        return ProjectInfoMaintenanceControllerResponseDto.Project.builder()
            .version(buildProjectInfoMaintenanceControllerVersionResponseDto().build())
            .runtimeMode("JVM");
    }

    public static ProjectInfoMaintenanceControllerResponseDto.Version.VersionBuilder buildProjectInfoMaintenanceControllerVersionResponseDto() {
        return ProjectInfoMaintenanceControllerResponseDto.Version.builder()
            .current("1.0.0")
            .latest("1.0.0")
            .isLatest(true);
    }
}
