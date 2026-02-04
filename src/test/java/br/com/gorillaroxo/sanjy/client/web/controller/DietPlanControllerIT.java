package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealTypeResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealTypeControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.DietPlanControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealTypeControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.client.web.test.builder.DtoBuilders;
import br.com.gorillaroxo.sanjy.client.web.test.builder.DtoControllerBuilders;
import br.com.gorillaroxo.sanjy.client.web.util.ExceptionCode;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class DietPlanControllerIT extends IntegrationTestController {

    private static final String RESOURCE_URL = "/api/v1/diet-plan";

    @Nested
    @DisplayName("POST /api/v1/diet-plan")
    class CreateDietPlan {

        @Test
        @DisplayName("Should create diet plan successfully and return correct response structure")
        void should_create_diet_plan_successfully() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody = DtoControllerBuilders.buildDietPlanControllerRequestDto().build();

            final DietPlanResponseDto expectedDietPlan = dietPlanRestClientMock.newDietPlan().success(uuid);
            final MealTypeResponseDto expectedMealType = expectedDietPlan.mealTypes().stream().findFirst().orElseThrow();

            webTestClient
                    .post()
                    .uri(RESOURCE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectBody(DietPlanControllerResponseDto.class)
                    .value(actualDietPlan -> {
                        assertThat(actualDietPlan.id()).isEqualTo(DtoBuilders.DIET_PLAN_ID);
                        assertThat(actualDietPlan.name()).isEqualTo(expectedDietPlan.name());
                        assertThat(actualDietPlan.dailyCalories()).isEqualTo(expectedDietPlan.dailyCalories());
                        assertThat(actualDietPlan.dailyProteinInG()).isEqualTo(expectedDietPlan.dailyProteinInG());
                        assertThat(actualDietPlan.isActive()).isTrue();
                        assertThat(actualDietPlan.mealTypes())
                            .isNotNull()
                            .isNotEmpty()
                            .hasSameSizeAs(expectedDietPlan.mealTypes());

                        final MealTypeControllerResponseDto actualMealType = actualDietPlan.mealTypes().stream().findFirst().orElseThrow();
                        assertThat(actualMealType.id()).isEqualTo(DtoBuilders.MEAL_TYPE_ID);
                        assertThat(actualMealType.name()).isEqualTo(expectedMealType.name());
                        assertThat(actualMealType.standardOptions())
                            .isNotNull()
                            .isNotEmpty()
                            .hasSameSizeAs(expectedMealType.standardOptions());

                        assertThat(actualDietPlan.metadata()).isNotNull();
                        assertThat(actualDietPlan.metadata().createdAt()).isNotNull();
                        assertThat(actualDietPlan.metadata().updatedAt()).isNotNull();
                    });
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", ""})
        @DisplayName("Should return 400 when request body has invalid name")
        void should_return_bad_request_when_name_is_invalid(final String name) {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody = DtoControllerBuilders.buildDietPlanControllerRequestDto()
                    .name(name)
                    .build();

            webTestClient
                    .post()
                    .uri(RESOURCE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        assertThat(actualErrorResponse.userCode()).isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage()).isNotEmpty().containsIgnoringCase("name");
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should return 400 when mealTypes is invalid")
        void should_return_bad_request_when_meal_types_is_invalid(final List<MealTypeControllerRequestDto> mealTypes) {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody = DtoControllerBuilders.buildDietPlanControllerRequestDto()
                    .mealTypes(mealTypes)
                    .build();

            webTestClient
                    .post()
                    .uri(RESOURCE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .value(actualErrorResponse -> {
                    assertThat(actualErrorResponse.userCode()).isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                    assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                    assertThat(actualErrorResponse.customMessage()).isNotEmpty().containsIgnoringCase("mealTypes");
                    assertThat(actualErrorResponse.timestamp()).isNotNull();
                });
        }

        @Test
        @DisplayName("Should return 500 when sanjy-server returns 4xx error")
        void should_return_internal_server_error_when_sanjy_server_returns_client_error() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody = DtoControllerBuilders.buildDietPlanControllerRequestDto().build();
            final var serverErrorResponse = jsonUtil.serialize(
                    DtoBuilders.buildSanjyServerErrorResponseDietPlanNotFoundDto().build());

            dietPlanRestClientMock.newDietPlan().generic(HttpStatus.BAD_REQUEST, uuid, serverErrorResponse);

            webTestClient
                    .post()
                    .uri(RESOURCE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                .expectBody(ErrorResponseDto.class)
                .value(actualErrorResponse -> {
                    final var exceptionCode = ExceptionCode.UNHANDLED_CLIENT_HTTP;

                    assertThat(actualErrorResponse.userCode()).isEqualTo(exceptionCode.getUserCode());
                    assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(actualErrorResponse.userMessage()).isNotEmpty().isEqualTo(exceptionCode.getUserMessage());
                    assertThat(actualErrorResponse.timestamp()).isNotNull();
                });
        }

        @Test
        @DisplayName("Should return 500 when sanjy-server returns 5xx error")
        void should_return_internal_server_error_when_sanjy_server_returns_server_error() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody = DtoControllerBuilders.buildDietPlanControllerRequestDto().build();
            final var serverErrorResponse = "{\"error\": \"Internal Server Error\"}";

            dietPlanRestClientMock.newDietPlan().generic(HttpStatus.INTERNAL_SERVER_ERROR, uuid, serverErrorResponse);

            webTestClient
                    .post()
                    .uri(RESOURCE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                .expectBody(ErrorResponseDto.class)
                .value(actualErrorResponse -> {
                    final var exceptionCode = ExceptionCode.UNHANDLED_CLIENT_HTTP;

                    assertThat(actualErrorResponse.userCode()).isEqualTo(exceptionCode.getUserCode());
                    assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(actualErrorResponse.userMessage()).isNotEmpty().isEqualTo(exceptionCode.getUserMessage());
                    assertThat(actualErrorResponse.timestamp()).isNotNull();
                });
        }
    }
}
