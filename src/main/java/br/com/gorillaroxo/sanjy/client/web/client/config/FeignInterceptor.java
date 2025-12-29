package br.com.gorillaroxo.sanjy.client.web.client.config;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientConfigProp;
import br.com.gorillaroxo.sanjy.client.web.util.DistributedTracingUtil;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeignInterceptor implements RequestInterceptor {

    private final DistributedTracingUtil distributedTracingUtil;
    private final SanjyClientConfigProp sanjyClientConfigProp;

    @Override
    public void apply(RequestTemplate template) {
        final String correlationId = distributedTracingUtil.getCorrelationId();

        template.header(RequestConstants.Headers.X_CORRELATION_ID, correlationId);
        template.header(RequestConstants.Headers.X_CHANNEL, sanjyClientConfigProp.application().channel());
    }
}
