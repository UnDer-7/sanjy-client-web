package br.com.gorillaroxo.sanjy.client.web.client.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
class GlobalRestClientConfig {

    private final ConnectivityErrorInterceptor connectivityErrorInterceptor;
    private final LogRestClientInterceptor logRestClientInterceptor;
    private final DefaultRestClientErrorHandler defaultRestClientErrorHandler;

    @Bean("globalRestClient")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestClient globalRestClient() {
        return RestClient.builder()
                .requestFactory(new BufferingClientHttpRequestFactory(new JdkClientHttpRequestFactory()))
                .requestInterceptors(interceptors -> {
                    interceptors.addLast(logRestClientInterceptor);
                    interceptors.addLast(connectivityErrorInterceptor);
                })
                .defaultStatusHandler(HttpStatusCode::isError, defaultRestClientErrorHandler::handler)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
