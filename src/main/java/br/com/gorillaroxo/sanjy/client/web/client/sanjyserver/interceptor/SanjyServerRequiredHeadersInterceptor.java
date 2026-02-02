package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.interceptor;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.util.DistributedTracingUtil;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SanjyServerRequiredHeadersInterceptor implements ClientHttpRequestInterceptor {

    private final DistributedTracingUtil distributedTracingUtil;
    private final SanjyClientWebConfigProp sanjyClientWebConfigProp;

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {
        final String correlationId = distributedTracingUtil.getCorrelationId();
        final String channel = sanjyClientWebConfigProp.application().channel();

        request.getHeaders().addAll(MultiValueMap.fromSingleValue(Map.of(
            RequestConstants.Headers.X_CORRELATION_ID, correlationId,
            RequestConstants.Headers.X_CHANNEL, channel)));

        return execution.execute(request, body);
    }

}
