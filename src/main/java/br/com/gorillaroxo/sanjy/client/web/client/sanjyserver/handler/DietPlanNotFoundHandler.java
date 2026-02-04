package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.handler;

import br.com.gorillaroxo.sanjy.client.web.client.config.BodyUtils;
import br.com.gorillaroxo.sanjy.client.web.client.config.DefaultRestClientErrorHandler;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.SanjyServerErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.exception.DietPlanNotFoundException;
import br.com.gorillaroxo.sanjy.client.web.util.JsonUtil;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DietPlanNotFoundHandler {

    private final DefaultRestClientErrorHandler defaultRestClientErrorHandler;
    private final JsonUtil jsonUtil;

    @SneakyThrows
    public void handler(final HttpRequest request, final ClientHttpResponse response) {
        if (canHandler(request, response)) {
            log.warn(
                    LogField.Placeholders.ONE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Diet Plan Not Found"));

            throw new DietPlanNotFoundException();
        }

        defaultRestClientErrorHandler.handler(request, response);
    }

    @SneakyThrows
    private boolean canHandler(final HttpRequest request, final ClientHttpResponse response) {
        if (!response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
            return false;
        }

        return jsonUtil.deserializeSafely(BodyUtils.readBody(response), SanjyServerErrorResponseDto.class)
                .map(SanjyServerErrorResponseDto::code)
                .filter(SanjyServerErrorResponseDto.ERROR_CODE_DIET_PLAN_NOT_FOUND::equals)
                .isPresent();
    }
}
