package br.com.gorillaroxo.sanjy.client.web.exception;

import br.com.gorillaroxo.sanjy.client.web.util.ExceptionCode;
import br.com.gorillaroxo.sanjy.client.web.util.JsonUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
public class UnhandledClientHttpException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.UNHANDLED_CLIENT_HTTP;
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    @Getter
    private final RequestInformation requestInformation;

    public UnhandledClientHttpException(
        final String customMessage, final Throwable originalCause, final RequestInformation requestInformation) {
        super(CODE, STATUS, customMessage, originalCause);
        this.requestInformation = requestInformation;
    }

    public UnhandledClientHttpException(final RequestInformation requestInformation) {
        super(CODE, STATUS);
        this.requestInformation = requestInformation;
    }

    public UnhandledClientHttpException(final Throwable originalCause, final RequestInformation requestInformation) {
        super(CODE, STATUS, originalCause);
        this.requestInformation = requestInformation;
    }

    public UnhandledClientHttpException(final String customMessage, final RequestInformation requestInformation) {
        super(CODE, STATUS, customMessage);
        this.requestInformation = requestInformation;
    }

    @Override
    protected LogLevel getLogLevel() {
        return LogLevel.ERROR;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    public static class RequestInformation implements Serializable {

        @Serial private static final long serialVersionUID = 1L;

        private final String requestMethod;
        private final String requestUrl;
        @Getter private final Set<Map.Entry<String, List<String>>> requestHeaders;

        private final Integer responseHttpStatusCode;
        @Getter private final Set<Map.Entry<String, List<String>>> responseHeaders;
        private final String responseBody;

        private final JsonUtil jsonUtil;

        @Builder
        public RequestInformation(
            final String requestMethod,
            final String requestUrl,
            final Set<Map.Entry<String, List<String>>> requestHeaders,

            final Integer responseHttpStatusCode,
            final Set<Map.Entry<String, List<String>>> responseHeaders,
            final String responseBody,

            final JsonUtil jsonUtil) {

            // Request info
            this.requestMethod = requestMethod;
            this.requestUrl = requestUrl;
            this.requestHeaders = Objects.requireNonNullElseGet(requestHeaders, Collections::emptySet);

            // Response info
            this.responseHttpStatusCode = responseHttpStatusCode;
            this.responseHeaders = Objects.requireNonNullElseGet(responseHeaders, Collections::emptySet);
            this.responseBody = responseBody;
            this.jsonUtil = jsonUtil;
        }

        public Optional<String> getRequestMethod() {
            return Optional.ofNullable(requestMethod).filter(Predicate.not(String::isBlank));
        }

        public Optional<String> getRequestUrl() {
            return Optional.ofNullable(requestUrl).filter(Predicate.not(String::isBlank));
        }

        public Optional<Integer> getResponseHttpStatusCode() {
            return Optional.ofNullable(responseHttpStatusCode);
        }

        public Optional<String> getResponseBody() {
            return Optional.ofNullable(responseBody).filter(Predicate.not(String::isBlank));
        }

        public <T> Optional<T> deserialize(final Class<T> clazz) {
            if (responseBody == null || responseBody.isBlank()) {
                return Optional.empty();
            }

            return jsonUtil.deserializeSafely(responseBody, clazz);
        }

    }

}
