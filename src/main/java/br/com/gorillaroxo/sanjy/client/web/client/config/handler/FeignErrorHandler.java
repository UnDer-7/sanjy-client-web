package br.com.gorillaroxo.sanjy.client.web.client.config.handler;

import br.com.gorillaroxo.sanjy.client.web.exception.BusinessException;
import feign.Response;

public interface FeignErrorHandler {

    BusinessException handle(Response response, String responseBodyJson);

    boolean canHandle(Response response, String responseBodyJson);
}
