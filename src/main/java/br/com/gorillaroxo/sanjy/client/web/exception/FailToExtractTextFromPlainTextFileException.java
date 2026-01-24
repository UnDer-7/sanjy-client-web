package br.com.gorillaroxo.sanjy.client.web.exception;

import br.com.gorillaroxo.sanjy.client.web.util.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

@Slf4j
public class FailToExtractTextFromPlainTextFileException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.FAIL_TO_EXTRACT_TEXT_FROM_PLAIN_TEXT_FILE;
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public FailToExtractTextFromPlainTextFileException(final String customMessage, final Throwable originalCause) {
        super(CODE, STATUS, customMessage, originalCause);
    }

    public FailToExtractTextFromPlainTextFileException() {
        super(CODE, STATUS);
    }

    public FailToExtractTextFromPlainTextFileException(final Throwable originalCause) {
        super(CODE, STATUS, originalCause);
    }

    public FailToExtractTextFromPlainTextFileException(final String customMessage) {
        super(CODE, STATUS, customMessage);
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
