package br.com.gorillaroxo.sanjy.client.web.exception;

import br.com.gorillaroxo.sanjy.client.web.util.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

@Slf4j
public class InvalidValuesException extends BusinessException {

    private static final ExceptionCode CODE = ExceptionCode.INVALID_VALUES;
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public InvalidValuesException(final String customMessage, final Throwable originalCause) {
        super(CODE, STATUS, customMessage, originalCause);
    }

    public InvalidValuesException() {
        super(CODE, STATUS);
    }

    public InvalidValuesException(final Throwable originalCause) {
        super(CODE, STATUS, originalCause);
    }

    public InvalidValuesException(final String customMessage) {
        super(CODE, STATUS, customMessage);
    }

    @Override
    protected LogLevel getLogLevel() {
        return LogLevel.WARN;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
