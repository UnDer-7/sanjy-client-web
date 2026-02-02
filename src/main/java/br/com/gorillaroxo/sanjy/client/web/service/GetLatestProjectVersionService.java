package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.github.client.GitHubReleaseRestClient;
import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetLatestProjectVersionService {

    private final GitHubReleaseRestClient gitHubReleaseRestClient;
    private final SanjyClientWebConfigProp configProp;

    public String clientWeb() {
        return gitHubReleaseRestClient
                .getLatestRelease(configProp.application().name())
                .tagName();
    }
}
