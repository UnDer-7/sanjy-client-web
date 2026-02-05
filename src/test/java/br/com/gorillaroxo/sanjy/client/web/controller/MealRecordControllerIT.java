package br.com.gorillaroxo.sanjy.client.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordCreatedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordCreatedControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.SearchMealRecordControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.client.web.test.builder.DtoControllerBuilders;
import br.com.gorillaroxo.sanjy.client.web.util.ExceptionCode;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Slf4j
class MealRecordControllerIT extends IntegrationTestController {

    private static final String RESOURCE_URL = "/api/v1/meal-record";

    @Nested
    @DisplayName("POST /api/v1/meal-record")
    class CreateMealRecord {

        @Test
        @DisplayName("Should create meal record successfully and return correct response structure")
        void should_create_meal_record_successfully() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody =
                    DtoControllerBuilders.buildMealRecordControllerRequestDtoFreeMeal().build();

            final MealRecordCreatedResponseDto expectedMealRecord =
                    mealRecordRestClientMock.newMealRecord().success(uuid);

            webTestClient
                    .post()
                    .uri(RESOURCE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectBody(MealRecordCreatedControllerResponseDto.class)
                    .value(actualMealRecord -> {
                        assertThat(actualMealRecord.id()).isEqualTo(expectedMealRecord.id());
                        assertThat(actualMealRecord.consumedAt()).isEqualTo(expectedMealRecord.consumedAt());
                        assertThat(actualMealRecord.mealType()).isNotNull();
                        assertThat(actualMealRecord.mealType().id())
                                .isEqualTo(expectedMealRecord.mealType().id());
                        assertThat(actualMealRecord.isFreeMeal()).isEqualTo(expectedMealRecord.isFreeMeal());
                        if (expectedMealRecord.standardOption() != null) {
                            assertThat(actualMealRecord.standardOption()).isNotNull();
                            assertThat(actualMealRecord.standardOption().id())
                                    .isEqualTo(expectedMealRecord.standardOption().id());
                        } else {
                            assertThat(actualMealRecord.standardOption()).isNull();
                        }
                        assertThat(actualMealRecord.freeMealDescription())
                                .isEqualTo(expectedMealRecord.freeMealDescription());
                        assertThat(actualMealRecord.quantity()).isEqualTo(expectedMealRecord.quantity());
                        assertThat(actualMealRecord.unit()).isEqualTo(expectedMealRecord.unit());
                        assertThat(actualMealRecord.notes()).isEqualTo(expectedMealRecord.notes());
                        assertThat(actualMealRecord.metadata()).isNotNull();
                        assertThat(actualMealRecord.metadata().createdAt())
                                .isEqualTo(expectedMealRecord.metadata().createdAt());
                        assertThat(actualMealRecord.metadata().updatedAt())
                                .isEqualTo(expectedMealRecord.metadata().updatedAt());
                    });
        }

        @Test
        @DisplayName("Should return 400 when mealTypeId is null")
        void should_return_bad_request_when_meal_type_id_is_null() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody = DtoControllerBuilders.buildMealRecordControllerRequestDtoFreeMeal()
                    .mealTypeId(null)
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
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage())
                                .isNotEmpty()
                                .containsIgnoringCase("mealTypeId");
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 400 when isFreeMeal is null")
        void should_return_bad_request_when_is_free_meal_is_null() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody = DtoControllerBuilders.buildMealRecordControllerRequestDtoFreeMeal()
                    .isFreeMeal(null)
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
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage())
                                .isNotEmpty()
                                .containsIgnoringCase("isFreeMeal");
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 400 when unit is blank")
        void should_return_bad_request_when_unit_is_blank() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody = DtoControllerBuilders.buildMealRecordControllerRequestDtoFreeMeal()
                    .unit("   ")
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
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage())
                                .isNotEmpty()
                                .containsIgnoringCase("unit");
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 500 when sanjy-server returns 4xx error")
        void should_return_internal_server_error_when_sanjy_server_returns_client_error() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody =
                    DtoControllerBuilders.buildMealRecordControllerRequestDtoFreeMeal().build();

            mealRecordRestClientMock.newMealRecord().genericBadRequest(uuid);

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
                        assertThat(actualErrorResponse.httpStatusCode())
                                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        assertThat(actualErrorResponse.userMessage())
                                .isNotEmpty()
                                .isEqualTo(exceptionCode.getUserMessage());
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 500 when sanjy-server returns 5xx error")
        void should_return_internal_server_error_when_sanjy_server_returns_server_error() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody =
                    DtoControllerBuilders.buildMealRecordControllerRequestDtoFreeMeal().build();

            mealRecordRestClientMock.newMealRecord().genericInternalServerError(uuid);

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
                        assertThat(actualErrorResponse.httpStatusCode())
                                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        assertThat(actualErrorResponse.userMessage())
                                .isNotEmpty()
                                .isEqualTo(exceptionCode.getUserMessage());
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 400 when X-Correlation-ID header is missing")
        void should_return_bad_request_when_correlation_id_header_is_missing() {
            final var requestBody =
                    DtoControllerBuilders.buildMealRecordControllerRequestDtoFreeMeal().build();

            webTestClient
                    .post()
                    .uri(RESOURCE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage())
                                .isNotEmpty()
                                .containsIgnoringCase(RequestConstants.Headers.X_CORRELATION_ID);
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }
    }

    @Nested
    @DisplayName("GET /api/v1/meal-record")
    class SearchMealRecords {

        @Test
        @DisplayName("Should search meal records successfully with both planned and free meals")
        void should_search_meal_records_successfully_with_both_planned_and_free_meals() {
            final var uuid = UUID.randomUUID().toString();
            final var consumedAtAfter = ZonedDateTime.now().minusDays(30);
            final var consumedAtBefore = ZonedDateTime.now();

            final PagedResponseDto<MealRecordResponseDto> expectedPagedResponse =
                    mealRecordRestClientMock.searchMealRecords().success(uuid);
            final MealRecordStatisticsResponseDto expectedStatistics =
                    mealRecordRestClientMock.mealRecordStatistics().success(uuid);

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_URL)
                            .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                            .queryParam(RequestConstants.Query.PAGE_SIZE, 10)
                            .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, consumedAtAfter.toString())
                            .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, consumedAtBefore.toString())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(SearchMealRecordControllerResponseDto.class)
                    .value(actualResponse -> {
                        // Validate page structure
                        assertThat(actualResponse.page()).isNotNull();
                        assertThat(actualResponse.page().totalPages()).isEqualTo(expectedPagedResponse.totalPages());
                        assertThat(actualResponse.page().currentPage()).isEqualTo(expectedPagedResponse.currentPage());
                        assertThat(actualResponse.page().pageSize()).isEqualTo(expectedPagedResponse.pageSize());
                        assertThat(actualResponse.page().totalItems()).isEqualTo(expectedPagedResponse.totalItems());
                        assertThat(actualResponse.page().content()).isNotNull();
                        assertThat(actualResponse.page().content())
                                .hasSameSizeAs(expectedPagedResponse.content());

                        // Validate free meal record
                        final MealRecordControllerResponseDto actualFreeMeal =
                                actualResponse.page().content().stream()
                                        .filter(MealRecordControllerResponseDto::isFreeMeal)
                                        .findFirst()
                                        .orElseThrow();
                        final MealRecordResponseDto expectedFreeMeal =
                                expectedPagedResponse.content().stream()
                                        .filter(MealRecordResponseDto::isFreeMeal)
                                        .findFirst()
                                        .orElseThrow();

                        assertThat(actualFreeMeal.id()).isEqualTo(expectedFreeMeal.id());
                        assertThat(actualFreeMeal.consumedAt()).isEqualTo(expectedFreeMeal.consumedAt());
                        assertThat(actualFreeMeal.isFreeMeal()).isTrue();
                        assertThat(actualFreeMeal.freeMealDescription())
                                .isEqualTo(expectedFreeMeal.freeMealDescription());
                        assertThat(actualFreeMeal.standardOption()).isNull();
                        assertThat(actualFreeMeal.quantity()).isEqualTo(expectedFreeMeal.quantity());
                        assertThat(actualFreeMeal.unit()).isEqualTo(expectedFreeMeal.unit());
                        assertThat(actualFreeMeal.notes()).isEqualTo(expectedFreeMeal.notes());
                        assertThat(actualFreeMeal.mealType()).isNotNull();
                        assertThat(actualFreeMeal.mealType().id())
                                .isEqualTo(expectedFreeMeal.mealType().id());
                        assertThat(actualFreeMeal.mealType().name())
                                .isEqualTo(expectedFreeMeal.mealType().name());
                        assertThat(actualFreeMeal.mealType().scheduledTime())
                                .isEqualTo(expectedFreeMeal.mealType().scheduledTime());
                        assertThat(actualFreeMeal.mealType().observation())
                                .isEqualTo(expectedFreeMeal.mealType().observation());
                        assertThat(actualFreeMeal.metadata()).isNotNull();
                        assertThat(actualFreeMeal.metadata().createdAt())
                                .isEqualTo(expectedFreeMeal.metadata().createdAt());
                        assertThat(actualFreeMeal.metadata().updatedAt())
                                .isEqualTo(expectedFreeMeal.metadata().updatedAt());

                        // Validate planned meal record
                        final MealRecordControllerResponseDto actualPlannedMeal =
                                actualResponse.page().content().stream()
                                        .filter(m -> !m.isFreeMeal())
                                        .findFirst()
                                        .orElseThrow();
                        final MealRecordResponseDto expectedPlannedMeal =
                                expectedPagedResponse.content().stream()
                                        .filter(m -> !m.isFreeMeal())
                                        .findFirst()
                                        .orElseThrow();

                        assertThat(actualPlannedMeal.id()).isEqualTo(expectedPlannedMeal.id());
                        assertThat(actualPlannedMeal.consumedAt()).isEqualTo(expectedPlannedMeal.consumedAt());
                        assertThat(actualPlannedMeal.isFreeMeal()).isFalse();
                        assertThat(actualPlannedMeal.freeMealDescription()).isNull();
                        assertThat(actualPlannedMeal.standardOption()).isNotNull();
                        assertThat(actualPlannedMeal.standardOption().id())
                                .isEqualTo(expectedPlannedMeal.standardOption().id());
                        assertThat(actualPlannedMeal.standardOption().optionNumber())
                                .isEqualTo(expectedPlannedMeal.standardOption().optionNumber());
                        assertThat(actualPlannedMeal.standardOption().description())
                                .isEqualTo(expectedPlannedMeal.standardOption().description());
                        assertThat(actualPlannedMeal.quantity()).isEqualTo(expectedPlannedMeal.quantity());
                        assertThat(actualPlannedMeal.unit()).isEqualTo(expectedPlannedMeal.unit());
                        assertThat(actualPlannedMeal.notes()).isEqualTo(expectedPlannedMeal.notes());
                        assertThat(actualPlannedMeal.mealType()).isNotNull();
                        assertThat(actualPlannedMeal.mealType().id())
                                .isEqualTo(expectedPlannedMeal.mealType().id());
                        assertThat(actualPlannedMeal.metadata()).isNotNull();

                        // Validate statistics
                        assertThat(actualResponse.mealRecordStatistics()).isNotNull();
                        assertThat(actualResponse.mealRecordStatistics().freeMealQuantity())
                                .isEqualTo(expectedStatistics.freeMealQuantity());
                        assertThat(actualResponse.mealRecordStatistics().plannedMealQuantity())
                                .isEqualTo(expectedStatistics.plannedMealQuantity());
                        assertThat(actualResponse.mealRecordStatistics().mealQuantity())
                                .isEqualTo(expectedStatistics.mealQuantity());
                    });
        }

        @Test
        @DisplayName("Should search meal records successfully with empty results")
        void should_search_meal_records_successfully_with_empty_results() {
            final var uuid = UUID.randomUUID().toString();
            final var consumedAtAfter = ZonedDateTime.now().minusDays(30);
            final var consumedAtBefore = ZonedDateTime.now();

            final PagedResponseDto<MealRecordResponseDto> expectedPagedResponse =
                    mealRecordRestClientMock.searchMealRecords().successEmpty(uuid);
            final MealRecordStatisticsResponseDto expectedStatistics =
                    mealRecordRestClientMock.mealRecordStatistics().successEmpty(uuid);

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_URL)
                        .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                        .queryParam(RequestConstants.Query.PAGE_SIZE, 10)
                        .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, consumedAtAfter.toString())
                        .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, consumedAtBefore.toString())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(SearchMealRecordControllerResponseDto.class)
                    .value(actualResponse -> {
                        // Validate page structure
                        assertThat(actualResponse.page()).isNotNull();
                        assertThat(actualResponse.page().totalPages()).isEqualTo(expectedPagedResponse.totalPages());
                        assertThat(actualResponse.page().currentPage()).isEqualTo(expectedPagedResponse.currentPage());
                        assertThat(actualResponse.page().pageSize()).isEqualTo(expectedPagedResponse.pageSize());
                        assertThat(actualResponse.page().totalItems()).isEqualTo(expectedPagedResponse.totalItems());
                        assertThat(actualResponse.page().content()).isNotNull();
                        assertThat(actualResponse.page().content()).isEmpty();

                        // Validate statistics
                        assertThat(actualResponse.mealRecordStatistics()).isNotNull();
                        assertThat(actualResponse.mealRecordStatistics().freeMealQuantity())
                                .isEqualTo(expectedStatistics.freeMealQuantity());
                        assertThat(actualResponse.mealRecordStatistics().plannedMealQuantity())
                                .isEqualTo(expectedStatistics.plannedMealQuantity());
                        assertThat(actualResponse.mealRecordStatistics().mealQuantity())
                                .isEqualTo(expectedStatistics.mealQuantity());
                    });
        }

        @Test
        @DisplayName("Should return 400 when pageNumber is null")
        void should_return_bad_request_when_page_number_is_null() {
            final var uuid = UUID.randomUUID().toString();
            final var consumedAtAfter = ZonedDateTime.now().minusDays(30);
            final var consumedAtBefore = ZonedDateTime.now();

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_URL)
                        .queryParam(RequestConstants.Query.PAGE_SIZE, 10)
                        .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, consumedAtAfter.toString())
                        .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, consumedAtBefore.toString())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage())
                                .isNotEmpty()
                                .containsIgnoringCase("pageNumber");
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 400 when consumedAtAfter is null")
        void should_return_bad_request_when_consumed_at_after_is_null() {
            final var uuid = UUID.randomUUID().toString();
            final var consumedAtBefore = ZonedDateTime.now();

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_URL)
                        .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                        .queryParam(RequestConstants.Query.PAGE_SIZE, 10)
                        .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, consumedAtBefore.toString())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage())
                                .isNotEmpty()
                                .containsIgnoringCase("consumedAtAfter");
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 400 when consumedAtBefore is null")
        void should_return_bad_request_when_consumed_at_before_is_null() {
            final var uuid = UUID.randomUUID().toString();
            final var consumedAtAfter = ZonedDateTime.now().minusDays(30);

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_URL)
                        .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                        .queryParam(RequestConstants.Query.PAGE_SIZE, 10)
                        .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, consumedAtAfter.toString())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage())
                                .isNotEmpty()
                                .containsIgnoringCase("consumedAtBefore");
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 500 when sanjy-server search returns 4xx error")
        void should_return_internal_server_error_when_search_returns_client_error() {
            final var uuid = UUID.randomUUID().toString();
            final var consumedAtAfter = ZonedDateTime.now().minusDays(30);
            final var consumedAtBefore = ZonedDateTime.now();

            mealRecordRestClientMock.searchMealRecords().genericBadRequest(uuid);
            mealRecordRestClientMock.mealRecordStatistics().success(uuid);

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_URL)
                        .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                        .queryParam(RequestConstants.Query.PAGE_SIZE, 10)
                        .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, consumedAtAfter.toString())
                        .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, consumedAtBefore.toString())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        final var exceptionCode = ExceptionCode.UNHANDLED_CLIENT_HTTP;

                        assertThat(actualErrorResponse.userCode()).isEqualTo(exceptionCode.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode())
                                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        assertThat(actualErrorResponse.userMessage())
                                .isNotEmpty()
                                .isEqualTo(exceptionCode.getUserMessage());
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 500 when sanjy-server search returns 5xx error")
        void should_return_internal_server_error_when_search_returns_server_error() {
            final var uuid = UUID.randomUUID().toString();
            final var consumedAtAfter = ZonedDateTime.now().minusDays(30);
            final var consumedAtBefore = ZonedDateTime.now();

            mealRecordRestClientMock.searchMealRecords().genericInternalServerError(uuid);
            mealRecordRestClientMock.mealRecordStatistics().success(uuid);

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_URL)
                        .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                        .queryParam(RequestConstants.Query.PAGE_SIZE, 10)
                        .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, consumedAtAfter.toString())
                        .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, consumedAtBefore.toString())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        final var exceptionCode = ExceptionCode.UNHANDLED_CLIENT_HTTP;

                        assertThat(actualErrorResponse.userCode()).isEqualTo(exceptionCode.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode())
                                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        assertThat(actualErrorResponse.userMessage())
                                .isNotEmpty()
                                .isEqualTo(exceptionCode.getUserMessage());
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 500 when sanjy-server statistics returns 4xx error")
        void should_return_internal_server_error_when_statistics_returns_client_error() {
            final var uuid = UUID.randomUUID().toString();
            final var consumedAtAfter = ZonedDateTime.now().minusDays(30);
            final var consumedAtBefore = ZonedDateTime.now();

            mealRecordRestClientMock.searchMealRecords().success(uuid);
            mealRecordRestClientMock.mealRecordStatistics().genericBadRequest(uuid);

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_URL)
                        .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                        .queryParam(RequestConstants.Query.PAGE_SIZE, 10)
                        .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, consumedAtAfter.toString())
                        .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, consumedAtBefore.toString())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        final var exceptionCode = ExceptionCode.UNHANDLED_CLIENT_HTTP;

                        assertThat(actualErrorResponse.userCode()).isEqualTo(exceptionCode.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode())
                                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        assertThat(actualErrorResponse.userMessage())
                                .isNotEmpty()
                                .isEqualTo(exceptionCode.getUserMessage());
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 500 when sanjy-server statistics returns 5xx error")
        void should_return_internal_server_error_when_statistics_returns_server_error() {
            final var uuid = UUID.randomUUID().toString();
            final var consumedAtAfter = ZonedDateTime.now().minusDays(30);
            final var consumedAtBefore = ZonedDateTime.now();

            mealRecordRestClientMock.searchMealRecords().success(uuid);
            mealRecordRestClientMock.mealRecordStatistics().genericInternalServerError(uuid);

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_URL)
                        .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                        .queryParam(RequestConstants.Query.PAGE_SIZE, 10)
                        .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, consumedAtAfter.toString())
                        .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, consumedAtBefore.toString())
                            .build())
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        final var exceptionCode = ExceptionCode.UNHANDLED_CLIENT_HTTP;

                        assertThat(actualErrorResponse.userCode()).isEqualTo(exceptionCode.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode())
                                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        assertThat(actualErrorResponse.userMessage())
                                .isNotEmpty()
                                .isEqualTo(exceptionCode.getUserMessage());
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 400 when X-Correlation-ID header is missing")
        void should_return_bad_request_when_correlation_id_header_is_missing() {
            final var consumedAtAfter = ZonedDateTime.now().minusDays(30);
            final var consumedAtBefore = ZonedDateTime.now();

            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_URL)
                        .queryParam(RequestConstants.Query.PAGE_NUMBER, 0)
                        .queryParam(RequestConstants.Query.PAGE_SIZE, 10)
                        .queryParam(RequestConstants.Query.CONSUMED_AT_AFTER, consumedAtAfter.toString())
                        .queryParam(RequestConstants.Query.CONSUMED_AT_BEFORE, consumedAtBefore.toString())
                            .build())
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage())
                                .isNotEmpty()
                                .containsIgnoringCase(RequestConstants.Headers.X_CORRELATION_ID);
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }
    }
}
