package br.com.gorillaroxo.sanjy.client.web.client.config;

import br.com.gorillaroxo.sanjy.client.web.client.config.handler.FeignErrorHandler;
import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final SanjyClientWebConfigProp sanjyClientWebConfigProp;
    private final Set<FeignErrorHandler> errorHandlers;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder(errorHandlers);
    }

    @Bean
    public Retryer retryer() {
        final var httpRetryProp = sanjyClientWebConfigProp.httpRetry();

        return new FeignRetryer(httpRetryProp);
    }
}
