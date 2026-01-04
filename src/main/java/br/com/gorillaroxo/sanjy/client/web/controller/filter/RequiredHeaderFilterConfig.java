package br.com.gorillaroxo.sanjy.client.web.controller.filter;

import br.com.gorillaroxo.sanjy.client.web.exception.InvalidValuesException;
import br.com.gorillaroxo.sanjy.client.web.mapper.BusinessExceptionMapper;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Order(2)
@Configuration
public class RequiredHeaderFilterConfig extends OncePerRequestFilter {
    private final AntPathMatcher pathMatcher;
    private final ObjectMapper objectMapper;
    private final BusinessExceptionMapper businessExceptionMapper;

    public RequiredHeaderFilterConfig(
        final AntPathMatcher pathMatcher,
        final ObjectMapper objectMapper,
        final BusinessExceptionMapper businessExceptionMapper) {

        this.pathMatcher = pathMatcher;
        this.objectMapper = objectMapper;
        this.businessExceptionMapper = businessExceptionMapper;
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS.name())) {
            return true;
        }

        final var requestUrl = request.getRequestURI();
        return !pathMatcher.match("/api/**/*", requestUrl);
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        final boolean isValid = validateRequiredHeaders(request, response);
        if (isValid) {
            filterChain.doFilter(request, response);
        }
    }

    private static boolean isValidUuid(final String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException _) {
            return false;
        }
    }

    private boolean validateRequiredHeaders(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        final String correlationId = request.getHeader(RequestConstants.Headers.X_CORRELATION_ID);

        final List<String> missingHeaders = new ArrayList<>();

        if (correlationId == null || correlationId.isBlank()) {
            missingHeaders.add(RequestConstants.Headers.X_CORRELATION_ID);
        }

        if (!missingHeaders.isEmpty()) {
            sendMissingHeadersErrorResponse(response, missingHeaders);
            return false;
        }

        if (!isValidUuid(correlationId)) {
            sendInvalidUuidErrorResponse(response, correlationId);
            return false;
        }

        return true;
    }

    private void sendInvalidUuidErrorResponse(final HttpServletResponse response, final String invalidValue)
            throws IOException {
        final var errorResponse = new InvalidValuesException("Header '%s' must be a valid UUID format. Received: '%s'"
                .formatted(RequestConstants.Headers.X_CORRELATION_ID, invalidValue));

        errorResponse.executeLogging();

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(businessExceptionMapper.toDto(errorResponse)));
    }

    private void sendMissingHeadersErrorResponse(final HttpServletResponse response, final List<String> missingHeaders)
            throws IOException {
        final var errorResponse =
                new InvalidValuesException("Missing headers. Headers: %s are required".formatted(missingHeaders));

        errorResponse.executeLogging();

        response.setStatus(errorResponse.getHttpStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(businessExceptionMapper.toDto(errorResponse)));
    }

}
