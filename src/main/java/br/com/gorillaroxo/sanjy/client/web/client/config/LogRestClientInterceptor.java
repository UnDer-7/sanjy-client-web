package br.com.gorillaroxo.sanjy.client.web.client.config;

import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
class LogRestClientInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {
        final ClientHttpResponse response = execution.execute(request, body);

        if (response.getStatusCode().isError()) {
            log.warn(
                LogField.Placeholders.EIGHT.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "An HTTP call returned an error"),

                // Request info
                StructuredArguments.kv(LogField.REQUEST_METHOD.label(), request.getMethod().name()),
                StructuredArguments.kv(LogField.REQUEST_URL.label(), request.getURI().toString()),
                StructuredArguments.kv(LogField.REQUEST_HEADERS.label(), readHeaders(request.getHeaders().entrySet())),
                StructuredArguments.kv(LogField.REQUEST_BODY.label(), BodyUtils.readBody(body, request.getHeaders().getContentType(), "Could not read request body")),

                // Response info
                StructuredArguments.kv(LogField.RESPONSE_HTTP_STATUS_CODE.label(), getResponseStatusCode(response)),
                StructuredArguments.kv(LogField.RESPONSE_HEADERS.label(), readHeaders(response.getHeaders().entrySet())),
                StructuredArguments.kv(LogField.RESPONSE_BODY.label(), BodyUtils.readBody(response)));
        }

        return response;
    }

    private static Integer getResponseStatusCode(final ClientHttpResponse response) {
        try {
            return response.getStatusCode().value();
        } catch (IOException e) {
            log.warn(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Could not get response status code"),
                e);
            return null;
        }
    }

    private static String readHeaders(final Set<Map.Entry<String, List<String>>> headers) {
        return headers.stream()
            .map(entry -> "[%s: %s]".formatted(entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(" :: "));
    }

}
