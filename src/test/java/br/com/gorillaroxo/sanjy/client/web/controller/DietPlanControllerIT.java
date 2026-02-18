package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealTypeResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.DietPlanControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealTypeControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.DietPlanControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealTypeControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.client.web.test.builder.DtoBuilders;
import br.com.gorillaroxo.sanjy.client.web.test.builder.DtoControllerBuilders;
import br.com.gorillaroxo.sanjy.client.web.util.ExceptionCode;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;

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
            final var requestBody =
                    DtoControllerBuilders.buildDietPlanControllerRequestDto().build();

            final DietPlanResponseDto expectedDietPlan =
                    dietPlanRestClientMock.newDietPlan().success(uuid);
            final MealTypeResponseDto expectedMealType =
                    expectedDietPlan.mealTypes().stream().findFirst().orElseThrow();

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

                        final MealTypeControllerResponseDto actualMealType =
                                actualDietPlan.mealTypes().stream().findFirst().orElseThrow();
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
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage())
                                .isNotEmpty()
                                .containsIgnoringCase("name");
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
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.customMessage())
                                .isNotEmpty()
                                .containsIgnoringCase("mealTypes");
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 500 when sanjy-server returns 4xx error")
        void should_return_internal_server_error_when_sanjy_server_returns_client_error() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody =
                    DtoControllerBuilders.buildDietPlanControllerRequestDto().build();

            dietPlanRestClientMock.newDietPlan().genericBadRequest(uuid);

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
                    DtoControllerBuilders.buildDietPlanControllerRequestDto().build();

            dietPlanRestClientMock.newDietPlan().genericInternalServerError(uuid);

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
        @DisplayName("Should return 502 when sanjy-server is unreachable")
        void should_return_bad_gateway_when_sanjy_server_is_unreachable() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody =
                    DtoControllerBuilders.buildDietPlanControllerRequestDto().build();

            dietPlanRestClientMock.newDietPlan().connectionFailure();

            webTestClient
                    .post()
                    .uri(RESOURCE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(HttpStatus.BAD_GATEWAY)
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        final var exceptionCode = ExceptionCode.SERVICE_CONNECTIVITY;

                        assertThat(actualErrorResponse.userCode()).isEqualTo(exceptionCode.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY.value());
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
                    DtoControllerBuilders.buildDietPlanControllerRequestDto().build();

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

        @Test
        @DisplayName("Should return 400 when endDate is in the past")
        void should_return_bad_request_when_end_date_is_in_the_past() {
            final var uuid = UUID.randomUUID().toString();
            final var requestBody = DtoControllerBuilders.buildDietPlanControllerRequestDto()
                    .endDate(LocalDate.now().minusDays(1))
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
                                .containsIgnoringCase("endDate");
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }
    }

    @Nested
    @DisplayName("GET /api/v1/diet-plan")
    class GetActiveDietPlan {

        @Test
        @DisplayName("Should get active diet plan successfully and return correct response structure")
        void should_get_active_diet_plan_successfully() {
            final var uuid = UUID.randomUUID().toString();

            dietPlanRestClientMock.activeDietPlan().success(uuid);

            webTestClient
                    .get()
                    .uri(RESOURCE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(DietPlanControllerResponseDto.class)
                    .value(actualDietPlan -> {
                        assertThat(actualDietPlan.id()).isEqualTo(DtoBuilders.DIET_PLAN_ID);
                        assertThat(actualDietPlan.name()).isNotEmpty();
                        assertThat(actualDietPlan.isActive()).isTrue();
                        assertThat(actualDietPlan.mealTypes()).isNotNull().isNotEmpty();

                        final MealTypeControllerResponseDto actualMealType =
                                actualDietPlan.mealTypes().stream().findFirst().orElseThrow();
                        assertThat(actualMealType.id()).isEqualTo(DtoBuilders.MEAL_TYPE_ID);
                        assertThat(actualMealType.standardOptions()).isNotNull().isNotEmpty();

                        assertThat(actualDietPlan.metadata()).isNotNull();
                        assertThat(actualDietPlan.metadata().createdAt()).isNotNull();
                        assertThat(actualDietPlan.metadata().updatedAt()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 400 when X-Correlation-ID header is missing")
        void should_return_bad_request_when_correlation_id_header_is_missing() {
            webTestClient
                    .get()
                    .uri(RESOURCE_URL)
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

        @Test
        @DisplayName("Should return 404 when diet plan is not found")
        void should_return_not_found_when_diet_plan_not_found() {
            final var uuid = UUID.randomUUID().toString();

            dietPlanRestClientMock.activeDietPlan().dietPlanNotFound(uuid);

            webTestClient
                    .get()
                    .uri(RESOURCE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isNotFound()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        final var exceptionCode = ExceptionCode.DIET_PLAN_NOT_FOUND;

                        assertThat(actualErrorResponse.userCode()).isEqualTo(exceptionCode.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(actualErrorResponse.userMessage())
                                .isNotEmpty()
                                .isEqualTo(exceptionCode.getUserMessage());
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 502 when sanjy-server is unreachable")
        void should_return_bad_gateway_when_sanjy_server_is_unreachable() {
            final var uuid = UUID.randomUUID().toString();

            dietPlanRestClientMock.activeDietPlan().connectionFailure();

            webTestClient
                    .get()
                    .uri(RESOURCE_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(HttpStatus.BAD_GATEWAY)
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        final var exceptionCode = ExceptionCode.SERVICE_CONNECTIVITY;

                        assertThat(actualErrorResponse.userCode()).isEqualTo(exceptionCode.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY.value());
                        assertThat(actualErrorResponse.userMessage())
                                .isNotEmpty()
                                .isEqualTo(exceptionCode.getUserMessage());
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 500 when sanjy-server returns 4xx error")
        void should_return_internal_server_error_when_sanjy_server_returns_client_error() {
            final var uuid = UUID.randomUUID().toString();

            dietPlanRestClientMock.activeDietPlan().genericBadRequest(uuid);

            webTestClient
                    .get()
                    .uri(RESOURCE_URL)
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
        @DisplayName("Should return 500 when sanjy-server returns 5xx error")
        void should_return_internal_server_error_when_sanjy_server_returns_server_error() {
            final var uuid = UUID.randomUUID().toString();

            dietPlanRestClientMock.activeDietPlan().genericInternalServerError(uuid);

            webTestClient
                    .get()
                    .uri(RESOURCE_URL)
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
    }

    @Nested
    @DisplayName("POST /api/v1/diet-plan/extract")
    class ExtractDietPlanFromFile {

        private static final String EXTRACT_URL = RESOURCE_URL + "/extract";

        @Test
        @DisplayName("Should extract diet plan from text file successfully")
        void should_extract_diet_plan_from_text_file_successfully() throws IOException {
            final var uuid = UUID.randomUUID().toString();
            final var fileContent =
                    new ClassPathResource("files/diet-plan-sample.txt").getContentAsString(StandardCharsets.UTF_8);

            final MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder
                    .part("file", fileContent.getBytes(StandardCharsets.UTF_8))
                    .filename("diet-plan.txt")
                    .contentType(MediaType.TEXT_PLAIN);

            webTestClient
                    .post()
                    .uri(EXTRACT_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(DietPlanControllerRequestDto.class)
                    .value(actualDietPlan -> {
                        assertThat(actualDietPlan.name()).isNotEmpty();
                        assertThat(actualDietPlan.mealTypes())
                                .isNotNull()
                                .isNotEmpty()
                                .hasSizeGreaterThanOrEqualTo(1);
                        assertThat(actualDietPlan.mealTypes().getFirst().standardOptions())
                                .isNotNull()
                                .isNotEmpty();
                    });
        }

        @Test
        @DisplayName("Should extract diet plan from pdf file successfully")
        void should_extract_diet_plan_from_pdf_file_successfully() throws IOException {
            final var uuid = UUID.randomUUID().toString();
            final var fileBytes = new ClassPathResource("files/diet-plan-sample.pdf")
                    .getInputStream()
                    .readAllBytes();

            final MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("file", fileBytes).filename("diet-plan-sample.pdf").contentType(MediaType.APPLICATION_PDF);

            webTestClient
                    .post()
                    .uri(EXTRACT_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(DietPlanControllerRequestDto.class)
                    .value(actualDietPlan -> {
                        assertThat(actualDietPlan.name()).isNotEmpty();
                        assertThat(actualDietPlan.mealTypes())
                                .isNotNull()
                                .isNotEmpty()
                                .hasSizeGreaterThanOrEqualTo(1);
                        assertThat(actualDietPlan.mealTypes().getFirst().standardOptions())
                                .isNotNull()
                                .isNotEmpty();
                    });
        }

        @Test
        @DisplayName("Should return 400 when file is empty")
        void should_return_bad_request_when_file_is_empty() {
            final var uuid = UUID.randomUUID().toString();

            final MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("file", new byte[0]).filename("empty.txt").contentType(MediaType.TEXT_PLAIN);

            webTestClient
                    .post()
                    .uri(EXTRACT_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.INVALID_VALUES.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should return 400 when X-Correlation-ID header is missing")
        void should_return_bad_request_when_correlation_id_header_is_missing() {
            final MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder
                    .part("file", "some content".getBytes(StandardCharsets.UTF_8))
                    .filename("diet-plan.txt")
                    .contentType(MediaType.TEXT_PLAIN);

            webTestClient
                    .post()
                    .uri(EXTRACT_URL)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
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

        @Test
        @DisplayName("Should return error when file type is not supported")
        void should_return_error_when_file_type_is_not_supported() {
            final var uuid = UUID.randomUUID().toString();

            final MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder
                    .part("file", "some content".getBytes(StandardCharsets.UTF_8))
                    .filename("diet-plan.xml")
                    .contentType(MediaType.APPLICATION_XML);

            webTestClient
                    .post()
                    .uri(EXTRACT_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponseDto.class)
                    .value(actualErrorResponse -> {
                        assertThat(actualErrorResponse.userCode())
                                .isEqualTo(ExceptionCode.DIET_PLAN_EXTRACTOR_STRATEGY_NOT_FOUND.getUserCode());
                        assertThat(actualErrorResponse.httpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(actualErrorResponse.userMessage()).isNotEmpty();
                        assertThat(actualErrorResponse.timestamp()).isNotNull();
                    });
        }
    }
}
