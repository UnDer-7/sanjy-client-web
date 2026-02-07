package br.com.gorillaroxo.sanjy.client.web.client.config;

import br.com.gorillaroxo.sanjy.client.web.exception.ServiceConnectivityException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * Interceptor that catches connectivity errors ({@link IOException}) during HTTP request execution and converts them
 * into {@link ServiceConnectivityException}.
 *
 * <p>This interceptor ensures that connection failures (e.g., connection refused, DNS resolution failure, timeout) are
 * represented as domain-specific exceptions rather than generic Spring {@code ResourceAccessException}. By converting
 * the error at the {@code RestClient} level, callers can explicitly handle connectivity failures in their code via
 * try-catch, rather than having the error bubble up to the global exception handler.
 *
 * @see ServiceConnectivityException
 */
@Slf4j
@Component
class ConnectivityErrorInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
            throws IOException {
        try {
            return execution.execute(request, body);
        } catch (final IOException ex) {
            throw new ServiceConnectivityException(
                    ex, request.getMethod().name(), request.getURI().toString());
        }
    }
}
