package br.com.gorillaroxo.sanjy.client.web.client.config;

import br.com.gorillaroxo.sanjy.client.web.exception.UnhandledClientHttpException;
import br.com.gorillaroxo.sanjy.client.web.util.JsonUtil;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultRestClientErrorHandler {

    private final JsonUtil jsonUtil;

    public void handler(final HttpRequest request, final ClientHttpResponse response) {
        throw new UnhandledClientHttpException(UnhandledClientHttpException.RequestInformation.builder()
                .requestMethod(request.getMethod().name())
                .requestUrl(request.getURI().toString())
                .requestHeaders(request.getHeaders().entrySet())
                .responseHttpStatusCode(getResponseHttpStatusCode(response))
                .responseHeaders(response.getHeaders().entrySet())
                .responseBody(BodyUtils.readBody(response))
                .jsonUtil(jsonUtil)
                .build());
    }

    private static Integer getResponseHttpStatusCode(final ClientHttpResponse response) {
        try {
            return response.getStatusCode().value();
        } catch (final IOException e) {
            log.warn(
                    LogField.Placeholders.ONE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Could not get response status code"),
                    e);

            return null;
        }
    }
}
