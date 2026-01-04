package br.com.gorillaroxo.sanjy.client.web.controller.config;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.exception.BusinessException;
import br.com.gorillaroxo.sanjy.client.web.exception.FileMaxUploadSizeException;
import br.com.gorillaroxo.sanjy.client.web.exception.InvalidValuesException;
import br.com.gorillaroxo.sanjy.client.web.exception.UnexpectedErrorException;
import br.com.gorillaroxo.sanjy.client.web.mapper.BusinessExceptionMapper;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Global exception handler for the web application.
 * Catches all exceptions and returns a user-friendly error page.
 */
@Slf4j
@ControllerAdvice
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandlerConfig extends ResponseEntityExceptionHandler {

    private final SanjyClientWebConfigProp sanjyClientWebConfigProp;
    private final BusinessExceptionMapper businessExceptionMapper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(final Exception exception) {
        if (exception.getCause() instanceof BusinessException businessException) {
            final String warnMsg = """
                An unexpected exception occurred but with a BusinessException cause, \
                delegating to BusinessException handler
                """;

            log.warn(
                LogField.Placeholders.FIVE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), warnMsg),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), exception.getMessage()),
                StructuredArguments.kv(
                    LogField.EXCEPTION_CLASS.label(),
                    exception.getClass().getSimpleName()),
                StructuredArguments.kv(LogField.EXCEPTION_CAUSE.label(), exception.getCause()),
                StructuredArguments.kv(
                    LogField.EXCEPTION_CAUSE_MSG.label(),
                    exception.getCause().getMessage()),
                exception);

            return handleBusinessException(businessException);
        }

        log.warn(
            LogField.Placeholders.TWO.placeholder,
            StructuredArguments.kv(LogField.MSG.label(), "An unexpected exception occurred"),
            StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), exception.getMessage()),
            exception);

        final var unexpectedException = new UnexpectedErrorException(exception);
        return logExceptionAndBuild(unexpectedException);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(final BusinessException exception) {
        return logExceptionAndBuild(exception);
    }

    // Handle @NotNull, @NotEmpty, ... errors in request parameters
    // e.g.: @RequestHeader, @RequestParam, @PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleUnexpectedException(final ConstraintViolationException exception) {
        final String invalidValues = exception.getConstraintViolations().stream()
            .map(violation -> buildInvalidAttributeMessage(
                violation.getPropertyPath().toString().split("\\.")[1],
                violation.getMessage(),
                violation.getInvalidValue()))
            .collect(Collectors.joining(" | "));

        return logExceptionAndBuild(new InvalidValuesException(invalidValues));
    }

    // Handle @NotNull, @NotEmpty, ... errors in request body
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
        final MethodArgumentNotValidException ex,
        @Nullable final HttpHeaders headers,
        @Nullable final HttpStatusCode status,
        @Nullable final WebRequest request) {

        final String invalidValues = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> buildInvalidAttributeMessage(
                fieldError.getField(),
                Objects.requireNonNullElse(fieldError.getDefaultMessage(), "validation failed"),
                fieldError.getRejectedValue()))
            .collect(Collectors.joining(" | "));

        return logExceptionAndBuild(new InvalidValuesException(invalidValues));
    }

    // Handle invalid formats in request parameters
    // e.g.: @RequestHeader, @RequestParam, @PathVariable
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleInvalidRequestParameters(MethodArgumentTypeMismatchException exception) {
        if (Objects.equals(exception.getRequiredType(), ZoneId.class)) {
            return buildInvalidZoneIdResponse(exception, exception.getName(), exception.getValue());
        }

        if (Objects.equals(exception.getRequiredType(), Instant.class)) {
            return buildInvalidInstantResponse(exception, exception.getName(), exception.getValue());
        }

        if (Objects.equals(exception.getRequiredType(), LocalDate.class)) {
            return buildInvalidDateResponse(exception, exception.getName(), exception.getValue());
        }

        if (Objects.equals(exception.getRequiredType(), LocalTime.class)) {
            return buildInvalidTimeResponse(exception, exception.getName(), exception.getValue());
        }

        return buildInvalidRequestParameters(
            exception.getName(), exception.getMessage(), exception.getValue(), exception);
    }

    // Handle invalid formats in request body
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(
        final HttpMessageNotReadableException ex,
        final HttpHeaders headers,
        final HttpStatusCode status,
        final WebRequest request) {

        // Check if the cause is an InvalidFormatException (parsing error)
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            final Class<?> targetType = invalidFormatException.getTargetType();
            final String fieldName = invalidFormatException.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("."));

            final Object invalidValue = invalidFormatException.getValue();

            if (Objects.equals(targetType, ZoneId.class)) {
                return buildInvalidZoneIdResponse(ex, fieldName, invalidValue);
            }

            if (Objects.equals(targetType, Instant.class)) {
                return buildInvalidInstantResponse(ex, fieldName, invalidValue);
            }

            if (Objects.equals(targetType, LocalDate.class)) {
                return buildInvalidDateResponse(ex, fieldName, invalidValue);
            }

            if (Objects.equals(targetType, LocalTime.class)) {
                return buildInvalidTimeResponse(ex, fieldName, invalidValue);
            }

            // Handle other format errors generically
            return buildInvalidRequestParameters(
                fieldName, "invalid format for type " + targetType.getSimpleName(), invalidValue, ex);
        }

        // For other HttpMessageNotReadableException cases, use default handling
        final var invalidValuesException = new InvalidValuesException("Failed to read request: " + ex.getMessage(), ex);
        return logExceptionAndBuild(invalidValuesException);
    }

    // ToDo: Fix max upload error
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public ResponseEntity<Object> handleMaxUploadSizeExceededException(final MaxUploadSizeExceededException exception) {
//        log.warn(
//            LogField.Placeholders.FIVE.placeholder,
//            StructuredArguments.kv(LogField.MSG.label(), "Max upload size exceeded exception"),
//            StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), exception.getMessage()),
//            StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), exception.getClass().getSimpleName()),
//            StructuredArguments.kv(LogField.EXCEPTION_CAUSE.label(), exception.getCause()),
//            StructuredArguments.kv(LogField.EXCEPTION_CAUSE_MSG.label(), exception.getCause().getMessage()),
//            exception);
//
//        return handleBusinessException(new FileMaxUploadSizeException("max file size is: %sMB".formatted(
//            sanjyClientWebConfigProp.upload().maxFileSizeInMb()), exception));
//    }

    private static String buildInvalidAttributeMessage(
        final String attributeName, final String errMotive, final Object attributeValue) {

        if (errMotive.contains(Instant.class.getName())) {
            final String errMotiveInstant = "date-time must be in the following format: %s (example: %s)"
                .formatted(RequestConstants.DateTimeFormats.DATE_TIME_LOCAL_FORMAT, RequestConstants.Examples.DATE_TIME);

            return "[ propertyPath: %s - errorMotive: %s - valueProvided: %s ]"
                .formatted(attributeName, errMotiveInstant, attributeValue);
        }

        return "[ propertyPath: %s - errorMotive: %s - valueProvided: %s ]"
            .formatted(attributeName, errMotive, attributeValue);
    }

    private ResponseEntity<Object> buildInvalidZoneIdResponse(
        final Exception ex, final String fieldName, final Object invalidValue) {
        return buildInvalidRequestParameters(
            fieldName,
            "invalid timezone id",
            invalidValue,
            "timezone must be a valid tz database identifier (e.g., America/Sao_Paulo, UTC, Europe/London)",
            ex);
    }

    private ResponseEntity<Object> buildInvalidInstantResponse(
        final Exception ex, final String fieldName, final Object invalidValue) {
        final String description = "date-time must be in the following format: %s - example: %s"
            .formatted(RequestConstants.DateTimeFormats.DATE_TIME_LOCAL_FORMAT, RequestConstants.Examples.DATE_TIME);

        return buildInvalidRequestParameters(fieldName, "invalid date-time format", invalidValue, description, ex);
    }

    private ResponseEntity<Object> buildInvalidDateResponse(
        final Exception ex, final String fieldName, final Object invalidValue) {
        final String description = "date must be in the following format: %s - example: %s"
            .formatted(RequestConstants.DateTimeFormats.DATE_FORMAT, RequestConstants.Examples.DATE);

        return buildInvalidRequestParameters(fieldName, "invalid date format", invalidValue, description, ex);
    }

    private ResponseEntity<Object> buildInvalidTimeResponse(
        final Exception ex, final String fieldName, final Object invalidValue) {
        final String description = "time must be in the following format: %s - example: %s"
            .formatted(RequestConstants.DateTimeFormats.TIME_FORMAT, RequestConstants.Examples.TIME);

        return buildInvalidRequestParameters(fieldName, "invalid time format", invalidValue, description, ex);
    }


    private ResponseEntity<Object> buildInvalidRequestParameters(
        final String parameterName,
        final String motive,
        final Object valueProvided,
        final String description,
        final Exception originalException) {
        var msg = "[ parameter name: %s - errorMotive: %s - valueProvided: %s - description: %s ]"
            .formatted(parameterName, motive, valueProvided, description);

        final var invalidValuesException = new InvalidValuesException(msg, originalException);
        return logExceptionAndBuild(invalidValuesException);
    }

    private ResponseEntity<Object> buildInvalidRequestParameters(
        final String parameterName,
        final String motive,
        final Object valueProvided,
        final Exception originalException) {
        var msg = "[ parameter name: %s - errorMotive: %s - valueProvided: %s ]"
            .formatted(parameterName, motive, valueProvided);

        final var invalidValuesException = new InvalidValuesException(msg, originalException);
        return logExceptionAndBuild(invalidValuesException);
    }

    private ResponseEntity<Object> logExceptionAndBuild(final BusinessException exception) {
        try {
            MDC.put(LogField.ERROR_CODE.label(), exception.getExceptionCode().getUserCode());
            MDC.put(LogField.ERROR_TIMESTAMP.label(), exception.getTimestamp().toString());
            MDC.put(LogField.ERROR_MESSAGE.label(), exception.getExceptionCode().getUserMessage());
            MDC.put(LogField.HTTP_STATUS_CODE.label(), Integer.toString(exception.getHttpStatusCode()));
            MDC.put(
                LogField.CUSTOM_EXCEPTION_STACK_TRACE.label(),
                Arrays.stream(exception.getStackTrace())
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("; ")));
            exception
                .getCustomMessage()
                .ifPresent(customMsg -> MDC.put(LogField.CUSTOM_ERROR_MESSAGE.label(), customMsg));

            exception.executeLogging();

            final ErrorResponseDto errorDto = businessExceptionMapper.toDto(exception);
            return ResponseEntity.status(exception.getHttpStatusCode()).body(errorDto);
        } finally {
            MDC.clear();
        }
    }

}
