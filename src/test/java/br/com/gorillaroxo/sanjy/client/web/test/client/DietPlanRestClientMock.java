package br.com.gorillaroxo.sanjy.client.web.test.client;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.SanjyServerErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.test.builder.DtoBuilders;
import br.com.gorillaroxo.sanjy.client.web.test.mockwebserver.MockWebServerDispatcher;
import br.com.gorillaroxo.sanjy.client.web.util.JsonUtil;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import lombok.Getter;
import lombok.experimental.Accessors;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Mock client for DietPlanRestClient using MockWebServer. Similar to WireMock's stubbing approach but using
 * MockWebServer's Dispatcher pattern.
 */
@Getter
@Accessors(fluent = true)
public class DietPlanRestClientMock {

    private static final String BASE_PATH = "/sanjy-server/v1/diet-plan";

    private final MockWebServerDispatcher dispatcher;
    private final NewDietPlan newDietPlan;
    private final ActiveDietPlan activeDietPlan;
    private final JsonUtil jsonUtil;

    public DietPlanRestClientMock(
            final MockWebServer mockWebServer, final MockWebServerDispatcher dispatcher, final JsonUtil jsonUtil) {
        this.dispatcher = dispatcher;
        this.newDietPlan = new NewDietPlan(dispatcher);
        this.activeDietPlan = new ActiveDietPlan(dispatcher);
        this.jsonUtil = jsonUtil;
    }

    public void reset() {
        dispatcher.reset();
    }

    public class NewDietPlan {

        private static final String PATH = BASE_PATH;
        private final MockWebServerDispatcher dispatcher;

        NewDietPlan(final MockWebServerDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public DietPlanResponseDto success(final String xCorrelationId) {
            final var responseDto = DtoBuilders.buildDietPlanResponseDto().build();
            generic(HttpStatus.CREATED, xCorrelationId, jsonUtil.serialize(responseDto));
            return responseDto;
        }

        public SanjyServerErrorResponseDto genericInternalServerError(final String xCorrelationId) {
            final var responseDto =
                    DtoBuilders.buildSanjyServerErrorResponseDtoGeneric500().build();
            generic(HttpStatus.INTERNAL_SERVER_ERROR, xCorrelationId, jsonUtil.serialize(responseDto));
            return responseDto;
        }

        public SanjyServerErrorResponseDto genericBadRequest(final String xCorrelationId) {
            final var responseDto =
                    DtoBuilders.buildSanjyServerErrorResponseDtoGeneric400().build();
            generic(HttpStatus.BAD_REQUEST, xCorrelationId, jsonUtil.serialize(responseDto));
            return responseDto;
        }

        public void generic(final HttpStatus httpStatus, final String xCorrelationId, final String responseBody) {
            dispatcher.register(PATH, request -> {
                // Verify expected headers
                final String correlationId = request.getHeaders().get(RequestConstants.Headers.X_CORRELATION_ID);
                final String channel = request.getHeaders().get(RequestConstants.Headers.X_CHANNEL);

                if (correlationId == null || !correlationId.equals(xCorrelationId)) {
                    return new MockResponse.Builder()
                            .code(400)
                            .body("Expected X-Correlation-ID: " + xCorrelationId + ", but got: " + correlationId)
                            .build();
                }

                if (channel == null || channel.isEmpty()) {
                    return new MockResponse.Builder()
                            .code(400)
                            .body("Missing required header: " + RequestConstants.Headers.X_CHANNEL)
                            .build();
                }

                return new MockResponse.Builder()
                        .code(httpStatus.value())
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(responseBody)
                        .build();
            });
        }
    }

    public class ActiveDietPlan {

        private static final String PATH = BASE_PATH + "/active";
        private final MockWebServerDispatcher dispatcher;

        ActiveDietPlan(final MockWebServerDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public void success(final String xCorrelationId) {
            final var responseDto = DtoBuilders.buildDietPlanResponseDto().build();
            generic(HttpStatus.OK, xCorrelationId, jsonUtil.serialize(responseDto));
        }

        public SanjyServerErrorResponseDto genericInternalServerError(final String xCorrelationId) {
            final var responseDto =
                    DtoBuilders.buildSanjyServerErrorResponseDtoGeneric500().build();
            generic(HttpStatus.INTERNAL_SERVER_ERROR, xCorrelationId, jsonUtil.serialize(responseDto));
            return responseDto;
        }

        public SanjyServerErrorResponseDto genericBadRequest(final String xCorrelationId) {
            final var responseDto =
                    DtoBuilders.buildSanjyServerErrorResponseDtoGeneric400().build();
            generic(HttpStatus.BAD_REQUEST, xCorrelationId, jsonUtil.serialize(responseDto));
            return responseDto;
        }

        public SanjyServerErrorResponseDto dietPlanNotFound(final String xCorrelationId) {
            final var responseDto = DtoBuilders.buildSanjyServerErrorResponseDtoDietPlanNotFound()
                    .build();
            generic(HttpStatus.NOT_FOUND, xCorrelationId, jsonUtil.serialize(responseDto));
            return responseDto;
        }

        public void generic(final HttpStatus httpStatus, final String xCorrelationId, final String responseBody) {
            dispatcher.register(PATH, request -> {
                // Verify expected headers
                final String correlationId = request.getHeaders().get(RequestConstants.Headers.X_CORRELATION_ID);
                final String channel = request.getHeaders().get(RequestConstants.Headers.X_CHANNEL);

                if (correlationId == null || !correlationId.equals(xCorrelationId)) {
                    return new MockResponse.Builder()
                            .code(400)
                            .body("Expected X-Correlation-ID: " + xCorrelationId + ", but got: " + correlationId)
                            .build();
                }

                if (channel == null || channel.isEmpty()) {
                    return new MockResponse.Builder()
                            .code(400)
                            .body("Missing required header: " + RequestConstants.Headers.X_CHANNEL)
                            .build();
                }

                return new MockResponse.Builder()
                        .code(httpStatus.value())
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(responseBody)
                        .build();
            });
        }
    }
}
