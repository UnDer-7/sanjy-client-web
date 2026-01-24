package br.com.gorillaroxo.sanjy.client.web.exception;

import br.com.gorillaroxo.sanjy.client.web.util.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

@Slf4j
public class FailToExtractTextFromPdfFileException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.FAIL_TO_EXTRACT_TEXT_FROM_PDF_FILE;
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public FailToExtractTextFromPdfFileException(final String customMessage, final Throwable originalCause) {
        super(CODE, STATUS, customMessage, originalCause);
    }

    public FailToExtractTextFromPdfFileException() {
        super(CODE, STATUS);
    }

    public FailToExtractTextFromPdfFileException(final Throwable originalCause) {
        super(CODE, STATUS, originalCause);
    }

    public FailToExtractTextFromPdfFileException(final String customMessage) {
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
