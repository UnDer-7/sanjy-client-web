package br.com.gorillaroxo.sanjy.client.web.controller.filter;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Order(1)
@Configuration
@RequiredArgsConstructor
public class RequestLoggingFilterConfig extends OncePerRequestFilter {

    private final SanjyClientWebConfigProp configProp;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        putHeadersInMdc(request);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private void putHeadersInMdc(final HttpServletRequest request) {
        final String correlationId = request.getHeader(RequestConstants.Headers.X_CORRELATION_ID);

        MDC.put(LogField.TRANSACTION_ID.label(), UUID.randomUUID().toString());
        MDC.put(LogField.HTTP_REQUEST.label(), "%s %s".formatted(request.getMethod(), request.getRequestURI()));
        MDC.put(LogField.CORRELATION_ID.label(), correlationId);
        MDC.put(LogField.CHANNEL.label(), configProp.application().channel());
    }
}
