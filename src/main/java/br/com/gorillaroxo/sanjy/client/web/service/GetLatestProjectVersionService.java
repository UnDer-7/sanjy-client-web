package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.github.GitHubReleaseFeignClient;
import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetLatestProjectVersionService {

    private final GitHubReleaseFeignClient gitHubReleaseFeignClient;
    private final SanjyClientWebConfigProp configProp;

    public String clientWeb() {
        return gitHubReleaseFeignClient
                .getLatestRelease(configProp.application().name())
                .tagName();
    }
}
