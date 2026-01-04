package br.com.gorillaroxo.sanjy.client.web.exception;

import br.com.gorillaroxo.sanjy.client.web.util.ExceptionCode;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.AccessLevel;
import lombok.Getter;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final Instant timestamp;
    private final int httpStatusCode;
    private final Throwable originalCause;
    private final ExceptionCode exceptionCode;

    @Getter(AccessLevel.NONE)
    private final String customMessage;

    protected BusinessException(
        final ExceptionCode exceptionCode,
        final HttpStatus httpStatus,
        final String customMessage,
        final Throwable originalCause) {

        super(getExceptionMessage(exceptionCode, customMessage, originalCause), originalCause);

        this.exceptionCode = exceptionCode;
        this.timestamp = Instant.now();
        this.customMessage = customMessage;
        this.httpStatusCode = httpStatus.value();
        this.originalCause = originalCause;
    }

    protected BusinessException(
        final ExceptionCode exceptionCode,
        final HttpStatus httpStatus) {

        super(getExceptionMessage(exceptionCode));

        this.exceptionCode = exceptionCode;
        this.timestamp = Instant.now();
        this.customMessage = null;
        this.httpStatusCode = httpStatus.value();
        this.originalCause = null;
    }


    protected BusinessException(
        final ExceptionCode exceptionCode,
        final HttpStatus httpStatus,
        final Throwable originalCause) {

        super(getExceptionMessage(exceptionCode, originalCause), originalCause);

        this.exceptionCode = exceptionCode;
        this.timestamp = Instant.now();
        this.customMessage = null;
        this.httpStatusCode = httpStatus.value();
        this.originalCause = originalCause;
    }

    protected BusinessException(
        final ExceptionCode exceptionCode,
        final HttpStatus httpStatus,
        final String customMessage) {

        super(getExceptionMessage(exceptionCode, customMessage));

        this.exceptionCode = exceptionCode;
        this.timestamp = Instant.now();
        this.customMessage = customMessage;
        this.httpStatusCode = httpStatus.value();
        this.originalCause = null;
    }

    public void executeLogging() {
        final var className = this.getClass().getSimpleName();
        final var defaultMsg = "An exception has occurred";

        switch (getLogLevel()) {
            case TRACE -> getLogger().trace(
                LogField.Placeholders.THREE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), defaultMsg),
                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), className),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), super.getMessage()));
            case DEBUG -> getLogger().debug(
                LogField.Placeholders.THREE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), defaultMsg),
                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), className),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), super.getMessage()));
            case INFO -> getLogger().info(LogField.Placeholders.THREE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), defaultMsg),
                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), className),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), super.getMessage()));
            case WARN -> getLogger().warn(LogField.Placeholders.THREE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), defaultMsg),
                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), className),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), super.getMessage()));
            case ERROR -> getLogger().error(LogField.Placeholders.THREE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), defaultMsg),
                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), className),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), super.getMessage()));
        }
    }

    public Optional<String> getCustomMessage() {
        return Optional.ofNullable(customMessage);
    }

    protected abstract LogLevel getLogLevel();

    protected abstract Logger getLogger();

    private static String getExceptionMessage(final ExceptionCode exceptionCode) {
        return "[code: %s] - [msg: %s]".formatted(exceptionCode.getUserCode(), exceptionCode.getUserMessage());
    }

    private static String getExceptionMessage(final ExceptionCode exceptionCode, final Throwable throwable) {
        return "[code: %s] - [msg: %s] - [originalCause: %s]".formatted(exceptionCode.getUserCode(), exceptionCode.getUserMessage(), throwable.getMessage());
    }

    private static String getExceptionMessage(final ExceptionCode exceptionCode, final String customMessage) {
        return Optional.ofNullable(customMessage)
            .filter(Predicate.not(String::isBlank))
            .map(cm -> "[code: %s] - [msg: %s] - [customMsg: %s]"
                .formatted(exceptionCode.getUserCode(), exceptionCode.getUserMessage(), cm))
            .orElseGet(() -> getExceptionMessage(exceptionCode));
    }

    private static String getExceptionMessage(final ExceptionCode exceptionCode, final String customMessage, final Throwable originalCause) {
        return Optional.ofNullable(customMessage)
            .filter(Predicate.not(String::isBlank))
            .map(cm -> "[code: %s] - [msg: %s] - [customMsg: %s] - [originalCause: %s]"
                .formatted(exceptionCode.getUserCode(), exceptionCode.getUserMessage(), cm, originalCause.getMessage()))
            .orElseGet(() -> getExceptionMessage(exceptionCode, originalCause));
    }

    protected enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}
