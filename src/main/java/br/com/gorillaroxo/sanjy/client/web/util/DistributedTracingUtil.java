package br.com.gorillaroxo.sanjy.client.web.util;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedTracingUtil {

    public String getCorrelationId() {
        final String correlationId = MDC.get(LogField.CORRELATION_ID.label());

        if (correlationId == null || correlationId.isBlank()) {
            final String newCorrelationId = UUID.randomUUID().toString();
            log.warn(
                    LogField.Placeholders.TWO.placeholder,
                    StructuredArguments.kv(
                            LogField.MSG.label(),
                            "Could not get correlation id from MDC Context, correlation id is null or blank, creating new correlation id"),
                    StructuredArguments.kv(LogField.CORRELATION_ID.label(), newCorrelationId));
            MDC.put(LogField.CORRELATION_ID.label(), newCorrelationId);
            return newCorrelationId;
        }

        return correlationId;
    }
}
