package br.com.gorillaroxo.sanjy.client.web.test.builder;

import br.com.gorillaroxo.sanjy.client.web.client.github.dto.response.GitHubReleaseResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealTypeResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MetadataResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.SanjyServerErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.StandardOptionResponseDto;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;

public final class DtoBuilders {

    private DtoBuilders() {
        throw new IllegalStateException("Utility class");
    }

    public static final long DIET_PLAN_ID = 1L;
    public static final long MEAL_TYPE_ID = 1L;
    public static final long STANDARD_OPTION_ID = 1L;

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
}
