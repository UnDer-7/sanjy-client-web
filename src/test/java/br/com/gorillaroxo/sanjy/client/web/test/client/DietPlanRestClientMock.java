package br.com.gorillaroxo.sanjy.client.web.test.client;

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
 * Mock client for DietPlanRestClient using MockWebServer.
 * Similar to WireMock's stubbing approach but using MockWebServer's Dispatcher pattern.
 */
@Getter
@Accessors(fluent = true)
public class DietPlanRestClientMock {

    private static final String BASE_PATH = "/sanjy-server/v1/diet-plan";

    private final MockWebServerDispatcher dispatcher;
    private final NewDietPlan newDietPlan;
    private final ActiveDietPlan activeDietPlan;
    final JsonUtil jsonUtil;

    public DietPlanRestClientMock(MockWebServer mockWebServer, MockWebServerDispatcher dispatcher, final JsonUtil jsonUtil) {
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

        NewDietPlan(MockWebServerDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        /**
         * Stubs a successful POST response (HTTP 201 CREATED) for creating a new diet plan.
         *
         * @param xCorrelationId The expected X-Correlation-ID header value
         */
        public void success(final String xCorrelationId) {
            generic(HttpStatus.CREATED, xCorrelationId, jsonUtil.serialize(DtoBuilders.buildDietPlanResponseDto().build()));
        }

        /**
         * Stubs a generic POST response for creating a new diet plan.
         *
         * @param httpStatus The HTTP status to return
         * @param xCorrelationId The expected X-Correlation-ID header value
         * @param responseBody The JSON response body
         */
        public void generic(HttpStatus httpStatus, String xCorrelationId, String responseBody) {
            dispatcher.register(PATH, request -> {
                // Verify expected headers
                String correlationId = request.getHeaders().get(RequestConstants.Headers.X_CORRELATION_ID);
                String channel = request.getHeaders().get(RequestConstants.Headers.X_CHANNEL);

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

        ActiveDietPlan(MockWebServerDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        /**
         * Stubs a successful GET response (HTTP 200 OK) for retrieving the active diet plan.
         *
         * @param responseBody The JSON response body
         */
        public void success(String responseBody) {
            generic(HttpStatus.OK, responseBody);
        }

        /**
         * Stubs a generic GET response for retrieving the active diet plan.
         *
         * @param httpStatus The HTTP status to return
         * @param responseBody The JSON response body
         */
        public void generic(HttpStatus httpStatus, String responseBody) {
            dispatcher.register(PATH, request -> new MockResponse.Builder()
                    .code(httpStatus.value())
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(responseBody)
                    .build());
        }

        /**
         * Stubs a 404 NOT_FOUND response for when no active diet plan exists.
         */
        public void notFound() {
            dispatcher.register(PATH, request -> new MockResponse.Builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(jsonUtil.serialize(DtoBuilders.buildSanjyServerErrorResponseDietPlanNotFoundDto().build()))
                    .build());
        }
    }
}
