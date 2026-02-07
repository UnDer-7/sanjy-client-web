package br.com.gorillaroxo.sanjy.client.web.test.client;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordCreatedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.SanjyServerErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.test.builder.DtoBuilders;
import br.com.gorillaroxo.sanjy.client.web.test.mockwebserver.MockWebServerDispatcher;
import br.com.gorillaroxo.sanjy.client.web.util.JsonUtil;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import mockwebserver3.MockResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Getter
@Accessors(fluent = true)
public class MealRecordRestClientMock {

    private static final String BASE_PATH = "/sanjy-server/v1/meal-record";

    private final MockWebServerDispatcher dispatcher;
    private final NewMealRecord newMealRecord;
    private final GetTodayMealRecords getTodayMealRecords;
    private final SearchMealRecords searchMealRecords;
    private final MealRecordStatistics mealRecordStatistics;
    private final JsonUtil jsonUtil;

    public MealRecordRestClientMock(final MockWebServerDispatcher dispatcher, final JsonUtil jsonUtil) {
        this.dispatcher = dispatcher;
        this.newMealRecord = new NewMealRecord(dispatcher);
        this.getTodayMealRecords = new GetTodayMealRecords(dispatcher);
        this.searchMealRecords = new SearchMealRecords(dispatcher);
        this.mealRecordStatistics = new MealRecordStatistics(dispatcher);
        this.jsonUtil = jsonUtil;
    }

    public void reset() {
        dispatcher.reset();
    }

    public class NewMealRecord {

        private static final String PATH = BASE_PATH;
        private final MockWebServerDispatcher dispatcher;

        NewMealRecord(final MockWebServerDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public MealRecordCreatedResponseDto success(final String xCorrelationId) {
            final var responseDto =
                    DtoBuilders.buildMealRecordCreatedResponseDto().build();
            generic(HttpStatus.OK, xCorrelationId, jsonUtil.serialize(responseDto));
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

    public class GetTodayMealRecords {

        private static final String PATH = BASE_PATH + "/today";
        private final MockWebServerDispatcher dispatcher;

        GetTodayMealRecords(final MockWebServerDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public List<MealRecordResponseDto> success(final String xCorrelationId) {
            final var responseDto =
                    List.of(DtoBuilders.buildMealRecordResponseDtoPlanned().build());
            generic(HttpStatus.OK, xCorrelationId, jsonUtil.serialize(responseDto));
            return responseDto;
        }

        public List<MealRecordResponseDto> successEmpty(final String xCorrelationId) {
            final List<MealRecordResponseDto> responseDto = List.of();
            generic(HttpStatus.OK, xCorrelationId, jsonUtil.serialize(responseDto));
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

    public class SearchMealRecords {

        private static final String PATH = BASE_PATH;
        private final MockWebServerDispatcher dispatcher;

        SearchMealRecords(final MockWebServerDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public PagedResponseDto<MealRecordResponseDto> success(final String xCorrelationId) {
            final var responseDto =
                    DtoBuilders.buildPagedMealRecordResponseDto().build();
            generic(HttpStatus.OK, xCorrelationId, jsonUtil.serialize(responseDto));
            return responseDto;
        }

        public PagedResponseDto<MealRecordResponseDto> successEmpty(final String xCorrelationId) {
            final var responseDto =
                    DtoBuilders.buildPagedMealRecordResponseDtoEmpty().build();
            generic(HttpStatus.OK, xCorrelationId, jsonUtil.serialize(responseDto));
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

    public class MealRecordStatistics {

        private static final String PATH = BASE_PATH + "/statistics";
        private final MockWebServerDispatcher dispatcher;

        MealRecordStatistics(final MockWebServerDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public MealRecordStatisticsResponseDto success(final String xCorrelationId) {
            final var responseDto =
                    DtoBuilders.buildMealRecordStatisticsResponseDto().build();
            generic(HttpStatus.OK, xCorrelationId, jsonUtil.serialize(responseDto));
            return responseDto;
        }

        public MealRecordStatisticsResponseDto successEmpty(final String xCorrelationId) {
            final var responseDto =
                    DtoBuilders.buildMealRecordStatisticsResponseDtoEmpty().build();
            generic(HttpStatus.OK, xCorrelationId, jsonUtil.serialize(responseDto));
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
