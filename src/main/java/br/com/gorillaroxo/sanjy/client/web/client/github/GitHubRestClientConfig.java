package br.com.gorillaroxo.sanjy.client.web.client.github;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GitHubRestClientConfig {

    @Qualifier("globalRestClient")
    private final RestClient restClient;
    private final SanjyClientWebConfigProp configProp;

    @Bean("gitHubRestClient")
    public RestClient restClient() {
        return restClient.mutate()
            .baseUrl(configProp.externalHttpClients().github().url())
            .build();
    }
}
