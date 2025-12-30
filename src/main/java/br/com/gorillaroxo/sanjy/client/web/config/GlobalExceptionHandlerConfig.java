package br.com.gorillaroxo.sanjy.client.web.config;

import br.com.gorillaroxo.sanjy.client.web.exception.BusinessException;
import br.com.gorillaroxo.sanjy.client.web.exception.FileMaxUploadSizeException;
import br.com.gorillaroxo.sanjy.client.web.exception.UnexpectedErrorException;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Global exception handler for the web application.
 * Catches all exceptions and returns a user-friendly error page.
 */
@Slf4j
@ControllerAdvice
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandlerConfig {

    private final SanjyClientWebConfigProp sanjyClientWebConfigProp;

    @ExceptionHandler(BusinessException.class)
    public ModelAndView businessException(final BusinessException exception) {
        return handleBusinessException(exception);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions(final Exception exception) {
        if (exception.getCause() instanceof BusinessException businessException) {
            log.warn(
                LogField.Placeholders.FIVE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "An unexpected exception occurred but with a BusinessException cause, delegating to BusinessException handler"),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), exception.getMessage()),
                StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), exception.getClass().getSimpleName()),
                StructuredArguments.kv(LogField.EXCEPTION_CAUSE.label(), exception.getCause()),
                StructuredArguments.kv(LogField.EXCEPTION_CAUSE_MSG.label(), exception.getCause().getMessage()),
                exception);

            return handleBusinessException(businessException);
        }

        log.warn(
            LogField.Placeholders.TWO.placeholder,
            StructuredArguments.kv(LogField.MSG.label(), "An unexpected exception occurred"),
            StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), exception.getMessage()),
            exception);

        return handleBusinessException(new UnexpectedErrorException(exception));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxUploadSizeExceededException(final MaxUploadSizeExceededException exception) {
        log.warn(
            LogField.Placeholders.FIVE.placeholder,
            StructuredArguments.kv(LogField.MSG.label(), "Max upload size exceeded exception"),
            StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), exception.getMessage()),
            StructuredArguments.kv(LogField.EXCEPTION_CLASS.label(), exception.getClass().getSimpleName()),
            StructuredArguments.kv(LogField.EXCEPTION_CAUSE.label(), exception.getCause()),
            StructuredArguments.kv(LogField.EXCEPTION_CAUSE_MSG.label(), exception.getCause().getMessage()),
            exception);

        return handleBusinessException(new FileMaxUploadSizeException("max file size is: %sMB".formatted(
            sanjyClientWebConfigProp.upload().maxFileSizeInMb()), exception));
    }

    private static ModelAndView handleBusinessException(final BusinessException exception) {
        try {
            MDC.put(LogField.ERROR_CODE.label(), exception.getExceptionCode().getUserCode());
            MDC.put(LogField.ERROR_TIMESTAMP.label(), exception.getTimestamp());
            MDC.put(LogField.ERROR_MESSAGE.label(), exception.getExceptionCode().getUserMessage());
            MDC.put(LogField.HTTP_STATUS_CODE.label(), Integer.toString(exception.getHttpStatusCode()));
            MDC.put(LogField.CUSTOM_EXCEPTION_STACK_TRACE.label(), Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("; ")));
            exception.getCustomMessage().ifPresent(customMsg -> MDC.put(LogField.CUSTOM_ERROR_MESSAGE.label(), customMsg));

            exception.executeLogging();

            // Create model and view for error page
            ModelAndView modelAndView = new ModelAndView("error");

            // Only expose generic message to the client
            modelAndView.addObject("errorTimestamp", exception.getTimestamp());
            modelAndView.addObject("errorUserMessage", exception.getExceptionCode().getUserMessage());
            modelAndView.addObject("errorUserCode", exception.getExceptionCode().getUserCode());

            return modelAndView;
        } finally {
            MDC.clear();
        }
    }
}
