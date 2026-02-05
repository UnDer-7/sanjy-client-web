package br.com.gorillaroxo.sanjy.client.web.test.builder;

import br.com.gorillaroxo.sanjy.client.web.client.github.dto.response.GitHubReleaseResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.IdOnlyResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordCreatedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealTypeResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealTypeSimplifiedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MetadataResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.SanjyServerErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.StandardOptionResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.StandardOptionSimplifiedResponseDto;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.IdOnlyControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordCreatedControllerResponseDto;
import org.springframework.http.HttpStatus;

public final class DtoBuilders {

    private DtoBuilders() {
        throw new IllegalStateException("Utility class");
    }

    public static final long DIET_PLAN_ID = 1L;
    public static final long MEAL_TYPE_ID = 1L;
    public static final long STANDARD_OPTION_ID = 1L;
    public static final long MEAL_RECORD_ID = 1L;

    public static DietPlanResponseDto.DietPlanResponseDtoBuilder buildDietPlanResponseDto() {
        return DietPlanResponseDto.builder()
                .id(DIET_PLAN_ID)
                .name("Plan N°02 - Cutting")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(2))
                .dailyCalories(2266)
                .dailyProteinInG(186)
                .dailyCarbsInG(288)
                .dailyFatInG(30)
                .goal("Body fat reduction with muscle mass preservation")
                .nutritionistNotes(
                        "Additional notes or observations from the nutritionist. Example: Patient has lactose intolerance. Avoid dairy products")
                .mealTypes(Set.of(buildMealTypeResponseDto().build()))
                .isActive(true)
                .metadata(buildMetadataResponseDto().build());
    }

    public static MealTypeResponseDto.MealTypeResponseDtoBuilder buildMealTypeResponseDto() {
        return MealTypeResponseDto.builder()
                .id(MEAL_TYPE_ID)
                .name("Breakfast")
                .scheduledTime(LocalTime.now())
                .observation("30 g proteína | 20 g carbo | 5 g gordura | 250 kcal")
                .dietPlanId(DIET_PLAN_ID)
                .standardOptions(List.of(buildStandardOptionResponseDto().build()))
                .metadata(buildMetadataResponseDto().build());
    }

    public static StandardOptionResponseDto.StandardOptionResponseDtoBuilder buildStandardOptionResponseDto() {
        return StandardOptionResponseDto.builder()
                .id(STANDARD_OPTION_ID)
                .optionNumber(1L)
                .description(
                        "2 slices of whole grain bread + 2 scrambled eggs + 1 banana + 200ml of coffee without sugar")
                .mealTypeId(MEAL_TYPE_ID)
                .metadata(buildMetadataResponseDto().build());
    }

    public static MetadataResponseDto.MetadataResponseDtoBuilder buildMetadataResponseDto() {
        return MetadataResponseDto.builder().createdAt(Instant.now()).updatedAt(Instant.now());
    }

    public static GitHubReleaseResponseDto.GitHubReleaseResponseDtoBuilder buildGitHubReleaseResponseDto() {
        return GitHubReleaseResponseDto.builder()
                .url("https://google.com")
                .assetsUrl("https://google.com")
                .url("https://google.com")
                .id(222L)
                .tagName("v1.0.0");
    }

    public static SanjyServerErrorResponseDto.SanjyServerErrorResponseDtoBuilder
            buildSanjyServerErrorResponseDtoDietPlanNotFound() {
        return SanjyServerErrorResponseDto.builder()
                .code(SanjyServerErrorResponseDto.ERROR_CODE_DIET_PLAN_NOT_FOUND)
                .timestamp(LocalDate.now().toString())
                .message("Diet Plan not found")
                .customMessage(null)
                .httpStatusCode(HttpStatus.NOT_FOUND.value());
    }

    public static SanjyServerErrorResponseDto.SanjyServerErrorResponseDtoBuilder
            buildSanjyServerErrorResponseDtoGeneric500() {
        return SanjyServerErrorResponseDto.builder()
                .code(SanjyServerErrorResponseDto.UNEXPECTED_ERROR)
                .timestamp(LocalDate.now().toString())
                .message("An unexpected error occurred")
                .customMessage(null)
                .httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static SanjyServerErrorResponseDto.SanjyServerErrorResponseDtoBuilder
            buildSanjyServerErrorResponseDtoGeneric400() {
        return SanjyServerErrorResponseDto.builder()
                .code(SanjyServerErrorResponseDto.INVALID_VALUES)
                .timestamp(LocalDate.now().toString())
                .message("Invalid values")
                .customMessage(null)
                .httpStatusCode(HttpStatus.BAD_REQUEST.value());
    }

    public static MealTypeSimplifiedResponseDto.MealTypeSimplifiedResponseDtoBuilder
            buildMealTypeSimplifiedResponseDto() {
        return MealTypeSimplifiedResponseDto.builder()
                .id(MEAL_TYPE_ID)
                .name("Pre-workout snack")
                .scheduledTime(LocalTime.of(6, 20, 0))
                .observation("25g protein | 40g carbs | 3g fat | 285 kcal")
                .metadata(buildMetadataResponseDto().build());
    }

    public static MealTypeSimplifiedResponseDto.MealTypeSimplifiedResponseDtoBuilder
            buildMealTypeSimplifiedResponseDtoMinimal() {
        return MealTypeSimplifiedResponseDto.builder()
                .id(MEAL_TYPE_ID)
                .name("Breakfast")
                .scheduledTime(LocalTime.of(9, 30, 0))
                .observation("30 g proteína | 20 g carbo | 5 g gordura | 250 kcal")
                .metadata(buildMetadataResponseDto().build());
    }

    public static StandardOptionSimplifiedResponseDto.StandardOptionSimplifiedResponseDtoBuilder
            buildStandardOptionSimplifiedResponseDto() {
        return StandardOptionSimplifiedResponseDto.builder()
                .id(STANDARD_OPTION_ID)
                .optionNumber(1L)
                .description("Banana -- 1 unit (90g) | Whey protein isolate -- 30g | Oats -- 20g")
                .metadata(buildMetadataResponseDto().build());
    }

    public static IdOnlyResponseDto.IdOnlyResponseDtoBuilder buildIdOnlyResponseDto() {
        return IdOnlyResponseDto.builder()
            .id(2L);
    }

    public static MealRecordCreatedResponseDto.MealRecordCreatedResponseDtoBuilder buildMealRecordCreatedResponseDtoPlannedMeal() {
        return buildMealRecordCreatedResponseDto()
            .isFreeMeal(false)
            .freeMealDescription(null)
            .standardOption(buildIdOnlyResponseDto().id(DtoBuilders.STANDARD_OPTION_ID).build());
    }

    public static MealRecordCreatedResponseDto.MealRecordCreatedResponseDtoBuilder buildMealRecordCreatedResponseDtoFreeMeal() {
        return buildMealRecordCreatedResponseDto()
            .isFreeMeal(true)
            .freeMealDescription("BigMac")
            .standardOption(null);
    }

    public static MealRecordCreatedResponseDto.MealRecordCreatedResponseDtoBuilder buildMealRecordCreatedResponseDto() {
        return MealRecordCreatedResponseDto.builder()
            .id(MEAL_RECORD_ID)
            .consumedAt(Instant.now())
            .mealType(buildIdOnlyResponseDto().id(DtoBuilders.MEAL_TYPE_ID).build())
            .isFreeMeal(true)
            .standardOption(null)
            .freeMealDescription("pacote de biscoito")
            .quantity(1.0)
            .unit("serving")
            .notes(null)
            .metadata(buildMetadataResponseDto().build());
    }

    public static MealRecordResponseDto.MealRecordResponseDtoBuilder buildMealRecordResponseDtoPlanned() {
        return buildMealRecordResponseDto()
            .isFreeMeal(false)
            .standardOption(buildStandardOptionSimplifiedResponseDto().build())
            .freeMealDescription(null);
    }

    public static MealRecordResponseDto.MealRecordResponseDtoBuilder buildMealRecordResponseDtoFreeMeal() {
        return buildMealRecordResponseDto()
            .isFreeMeal(true)
            .standardOption(null)
            .freeMealDescription("BigMac");
    }

    public static MealRecordResponseDto.MealRecordResponseDtoBuilder buildMealRecordResponseDto() {
        return MealRecordResponseDto.builder()
                .id(MEAL_RECORD_ID)
                .consumedAt(Instant.now())
                .mealType(buildMealTypeSimplifiedResponseDtoMinimal().build())
                .isFreeMeal(true)
                .standardOption(null)
                .freeMealDescription("pacote de biscoito")
                .quantity(1.0)
                .unit("serving")
                .notes(null)
                .metadata(buildMetadataResponseDto().build());
    }

    public static PagedResponseDto.PagedResponseDtoBuilder<MealRecordResponseDto>
            buildPagedMealRecordResponseDto() {
        return PagedResponseDto.<MealRecordResponseDto>builder()
                .totalPages(27)
                .currentPage(0)
                .pageSize(2)
                .totalItems(132L)
                .content(List.of(
                        buildMealRecordResponseDtoFreeMeal().build(),
                        buildMealRecordResponseDtoPlanned().build()));
    }

    public static PagedResponseDto.PagedResponseDtoBuilder<MealRecordResponseDto>
            buildPagedMealRecordResponseDtoEmpty() {
        return PagedResponseDto.<MealRecordResponseDto>builder()
                .totalPages(0)
                .currentPage(0)
                .pageSize(5)
                .totalItems(0L)
                .content(List.of());
    }

    public static MealRecordStatisticsResponseDto.MealRecordStatisticsResponseDtoBuilder
            buildMealRecordStatisticsResponseDto() {
        return MealRecordStatisticsResponseDto.builder()
                .freeMealQuantity(16L)
                .plannedMealQuantity(116L)
                .mealQuantity(132L);
    }

    public static MealRecordStatisticsResponseDto.MealRecordStatisticsResponseDtoBuilder
            buildMealRecordStatisticsResponseDtoEmpty() {
        return MealRecordStatisticsResponseDto.builder()
                .freeMealQuantity(0L)
                .plannedMealQuantity(0L)
                .mealQuantity(0L);
    }
}
