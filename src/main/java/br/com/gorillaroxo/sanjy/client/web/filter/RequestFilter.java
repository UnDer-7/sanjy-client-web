package br.com.gorillaroxo.sanjy.client.web.filter;

import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String correlationId = Optional.ofNullable(request.getHeader(RequestConstants.Headers.X_CORRELATION_ID))
                .filter(Predicate.not(String::isBlank))
                .orElseGet(() -> {
                    final String uuid = UUID.randomUUID().toString();
                    log.debug(
                        LogField.Placeholders.TWO.placeholder,
                        StructuredArguments.kv(LogField.MSG.label(), "Correlation ID not found in headers, generating new one"),
                        StructuredArguments.kv(LogField.CORRELATION_ID.label(), uuid));
                    return uuid;
                });
            final String transactionId = UUID.randomUUID().toString();

            MDC.put(LogField.TRANSACTION_ID.label(), transactionId);
            MDC.put(LogField.HTTP_REQUEST.label(), "%s %s".formatted(request.getMethod(), request.getRequestURI()));
            MDC.put(LogField.CORRELATION_ID.label(), correlationId);

            response.setHeader(RequestConstants.Headers.X_CORRELATION_ID, correlationId);

            filterChain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }
}
