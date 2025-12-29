package br.com.gorillaroxo.sanjy.client.web.client.config.handler;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.SanjyServerErrorResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientConfigProp;
import br.com.gorillaroxo.sanjy.client.web.exception.BusinessException;
import br.com.gorillaroxo.sanjy.client.web.exception.DietPlanNotFoundException;
import br.com.gorillaroxo.sanjy.client.web.util.JsonUtil;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import feign.Request;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DietPlanNotFoundFeignErrorHandler implements FeignErrorHandler {

    private final SanjyClientConfigProp sanjyClientConfigProp;
    private final JsonUtil jsonUtil;

    @Override
    public BusinessException handle(final Response response, final String responseBodyJson) {
        log.warn(
            LogField.Placeholders.ONE.placeholder,
            StructuredArguments.kv(LogField.MSG.label(), "Diet Plan Not Found"));

        return new DietPlanNotFoundException();
    }

    @Override
    public boolean canHandle(final Response response, final String responseBodyJson) {
        final boolean isUrlInvalid = Optional.ofNullable(response)
            .map(Response::request)
            .map(Request::url)
            .filter(uri -> uri.contains(sanjyClientConfigProp.externalApis().sanjyServer().url()))
            .isEmpty();

        if (isUrlInvalid) {
            return false;
        }

        final boolean isStatusCodeInvalid = Optional.ofNullable(response)
            .map(Response::status)
            .filter(Predicate.isEqual(HttpStatus.NOT_FOUND.value()))
            .isEmpty();

        if (isStatusCodeInvalid) {
            return false;
        }

        return jsonUtil.deserializeSafely(responseBodyJson, SanjyServerErrorResponseDTO.class)
            .map(SanjyServerErrorResponseDTO::code)
            .filter(SanjyServerErrorResponseDTO.ERROR_CODE_DIET_PLAN_NOT_FOUND::equals)
            .isPresent();
    }

}
