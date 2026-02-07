package br.com.gorillaroxo.sanjy.client.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.BooleanWrapperControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ProjectInfoMaintenanceControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.client.web.util.ExceptionCode;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Slf4j
class MaintenanceControllerIT extends IntegrationTestController {

    static final String RESOURCE_URL = "/api/v1/maintenance";

    @Nested
    @DisplayName("GET /api/v1/maintenance/project-info")
    class ProjectInfo {

        private static final String PROJECT_INFO_URL = RESOURCE_URL + "/project-info";

        @Test
        @DisplayName("Should get project info successfully and return correct response structure")
        void should_get_project_info_successfully() {
            final var uuid = UUID.randomUUID().toString();

            final var expectedServerProjectInfo =
                    maintenanceRestClientMock.projectInfo().success(uuid);

            webTestClient
                    .get()
                    .uri(PROJECT_INFO_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(ProjectInfoMaintenanceControllerResponseDto.class)
                    .value(actual -> {
                        assertThat(actual.sanjyClientWeb()).isNotNull();
                        assertThat(actual.sanjyClientWeb().version()).isNotNull();
                        assertThat(actual.sanjyClientWeb().version().current()).isNotEmpty();
                        assertThat(actual.sanjyClientWeb().version().isLatest()).isNotNull();
                        assertThat(actual.sanjyClientWeb().runtimeMode()).isNotEmpty();

                        assertThat(actual.sanjyServer()).isNotNull();
                        assertThat(actual.sanjyServer().version()).isNotNull();
                        assertThat(actual.sanjyServer().version().current())
                                .isEqualTo(expectedServerProjectInfo.version().current());
                        assertThat(actual.sanjyServer().version().latest())
                                .isEqualTo(expectedServerProjectInfo.version().latest());
                        assertThat(actual.sanjyServer().version().isLatest())
                                .isEqualTo(expectedServerProjectInfo.version().isLatest());
                        assertThat(actual.sanjyServer().runtimeMode())
                                .isEqualTo(expectedServerProjectInfo.runtimeMode());
                    });
        }

        @Test
        @DisplayName("Should return 502 when sanjy-server is unreachable")
        void should_return_bad_gateway_when_sanjy_server_is_unreachable() {
            final var uuid = UUID.randomUUID().toString();

            maintenanceRestClientMock.projectInfo().connectionFailure();

            webTestClient
                    .get()
                    .uri(PROJECT_INFO_URL)
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

            maintenanceRestClientMock.projectInfo().genericBadRequest(uuid);

            webTestClient
                    .get()
                    .uri(PROJECT_INFO_URL)
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

            maintenanceRestClientMock.projectInfo().genericInternalServerError(uuid);

            webTestClient
                    .get()
                    .uri(PROJECT_INFO_URL)
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
            webTestClient
                    .get()
                    .uri(PROJECT_INFO_URL)
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
    @DisplayName("GET /api/v1/maintenance/ai/availability")
    class IsAvailable {

        private static final String AVAILABILITY_URL = RESOURCE_URL + "/ai/availability";

        @Test
        @DisplayName("Should return true when AI is available")
        void should_return_true_when_ai_is_available() {
            final var uuid = UUID.randomUUID().toString();

            webTestClient
                    .get()
                    .uri(AVAILABILITY_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(BooleanWrapperControllerResponseDto.class)
                    .value(actual -> {
                        assertThat(actual.value()).isTrue();
                    });
        }

        @Test
        @DisplayName("Should return 400 when X-Correlation-ID header is missing")
        void should_return_bad_request_when_correlation_id_header_is_missing() {
            webTestClient
                    .get()
                    .uri(AVAILABILITY_URL)
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
