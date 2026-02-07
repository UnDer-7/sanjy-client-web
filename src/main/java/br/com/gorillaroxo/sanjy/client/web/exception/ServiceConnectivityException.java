package br.com.gorillaroxo.sanjy.client.web.exception;

import br.com.gorillaroxo.sanjy.client.web.util.ExceptionCode;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a connectivity error occurs while communicating with an external service.
 * <p>
 * This exception is raised when the HTTP client cannot establish a connection to the target service
 * (e.g., connection refused, DNS resolution failure, timeout). Unlike {@link UnhandledClientHttpException},
 * which is thrown when the server responds with an HTTP error status (4xx or 5xx), this exception indicates
 * that no HTTP response was received at all.
 * <p>
 * This exception is automatically thrown by the global {@code RestClient} interceptor chain when an
 * {@link java.io.IOException} occurs during request execution.
 */
@Slf4j
public class ServiceConnectivityException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.SERVICE_CONNECTIVITY;
    private static final HttpStatus STATUS = HttpStatus.BAD_GATEWAY;

    private final String requestMethod;

    private final String requestUrl;

    public ServiceConnectivityException(
            final Throwable originalCause, final String requestMethod, final String requestUrl) {
        super(CODE, STATUS, originalCause);
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
    }

    public ServiceConnectivityException(
            final String customMessage,
            final Throwable originalCause,
            final String requestMethod,
            final String requestUrl) {
        super(CODE, STATUS, customMessage, originalCause);
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
    }

    public Optional<String> getRequestMethod() {
        return Optional.ofNullable(requestMethod).filter(Predicate.not(String::isBlank));
    }

    public Optional<String> getRequestUrl() {
        return Optional.ofNullable(requestUrl).filter(Predicate.not(String::isBlank));
    }

    @Override
    protected LogLevel getLogLevel() {
        return LogLevel.ERROR;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
