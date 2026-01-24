package br.com.gorillaroxo.sanjy.client.web.util;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;

@Slf4j
public final class LoggingHelper {

    private LoggingHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String loggingAndReturnControllerPagePath(final String pageName) {
        log.info(
                LogField.Placeholders.TWO.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "Rendering a page"),
                StructuredArguments.kv(LogField.PAGE_PATH.label(), pageName));

        return pageName;
    }
}
