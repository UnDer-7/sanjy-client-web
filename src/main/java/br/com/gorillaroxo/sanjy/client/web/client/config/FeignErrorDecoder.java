package br.com.gorillaroxo.sanjy.client.web.client.config;

import br.com.gorillaroxo.sanjy.client.web.client.config.handler.FeignErrorHandler;
import br.com.gorillaroxo.sanjy.client.web.exception.UnhandledClientHttpException;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import br.com.gorillaroxo.sanjy.client.web.util.function.FunctionWrapper;
import com.google.common.io.CharStreams;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;

@Slf4j
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final Set<FeignErrorHandler> errorHandlers;

    @Override
    public Exception decode(final String methodKey, final Response response) {
        final var requestOpt = Optional.ofNullable(response).map(Response::request);
        final var responseOpt = Optional.ofNullable(response);

        final var httpMethod =
                requestOpt.map(Request::httpMethod).map(Enum::name).orElse(null);
        final var responseBody = readResponseBody(response);
        final var statusCode = responseOpt.map(Response::status).orElse(null);
        final var responseHeaders = responseOpt
                .map(Response::headers)
                .map(FeignErrorDecoder::readHeaders)
                .orElse(null);
        final var requestHeaders = requestOpt
                .map(Request::headers)
                .map(FeignErrorDecoder::readHeaders)
                .orElse(null);
        final var requestBody = readRequestBody(response);
        final var requestUrl = requestOpt.map(Request::url).orElse(null);

        final Function<Object, String> wrapInString = value -> "( " + value + " )";

        log.warn(
                LogField.Placeholders.NINE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "An HTTP call returned an error"),
                StructuredArguments.kv(LogField.FEIGN_METHOD_KEY.label(), methodKey),
                StructuredArguments.kv(LogField.REQUEST_METHOD.label(), httpMethod),
                StructuredArguments.kv(LogField.REQUEST_URL.label(), requestUrl),
                StructuredArguments.kv(LogField.HTTP_STATUS_CODE.label(), statusCode),
                StructuredArguments.kv(LogField.REQUEST_HEADERS.label(), requestHeaders),
                StructuredArguments.kv(LogField.REQUEST_BODY.label(), wrapInString.apply(requestBody)),
                StructuredArguments.kv(LogField.RESPONSE_HEADERS.label(), responseHeaders),
                StructuredArguments.kv(LogField.RESPONSE_BODY.label(), responseBody));

        return errorHandlers.stream()
                .filter(errorHandler -> errorHandler.canHandle(response, responseBody))
                .findFirst()
                .map(errorHandler -> errorHandler.handle(response, responseBody))
                .orElseGet(
                        () -> new UnhandledClientHttpException(UnhandledClientHttpException.RequestInformation.builder()
                                .feignMethodKey(methodKey)
                                .requestMethod(httpMethod)
                                .requestUrl(requestUrl)
                                .httpStatusCode(statusCode)
                                .requestHeaders(requestOpt.map(Request::headers).orElse(null))
                                .requestBody(requestBody)
                                .responseHeaders(
                                        responseOpt.map(Response::headers).orElse(null))
                                .responseBody(responseBody)
                                .build()));
    }

    private static String readHeaders(final Map<String, Collection<String>> headers) {
        return headers.entrySet().stream()
                .map(entry -> "[%s: %s]".formatted(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(" :: "));
    }

    private static String readResponseBody(final Response response) {
        try {
            return Optional.ofNullable(response)
                    .map(Response::body)
                    .map(FunctionWrapper.wrap(body -> body.asReader(StandardCharsets.UTF_8)))
                    .map(FunctionWrapper.wrap(CharStreams::toString))
                    .orElse(null);
        } catch (final Exception e) {
            log.warn(
                    LogField.Placeholders.ONE.placeholder,
                    StructuredArguments.kv(LogField.MSG.label(), "Error while reading response body from request"),
                    e);
            return null;
        }
    }

    private static String readRequestBody(final Response response) {
        try {
            return Optional.ofNullable(response)
                    .map(Response::request)
                    .map(Request::body)
                    .map(String::new)
                    .orElse(null);
        } catch (final Exception e) {
            log.warn(
                    LogField.Placeholders.ONE.placeholder,
                    StructuredArguments.kv(LogField.MSG.label(), "Error while reading request body from request"),
                    e);
            return null;
        }
    }
}
