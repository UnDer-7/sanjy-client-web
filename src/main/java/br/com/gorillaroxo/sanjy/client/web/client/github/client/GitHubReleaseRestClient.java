package br.com.gorillaroxo.sanjy.client.web.client.github.client;

import br.com.gorillaroxo.sanjy.client.web.client.github.dto.response.GitHubReleaseResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubReleaseRestClient {

    public static final String CLIENT_URL = "/repos/UnDer-7";

    @Qualifier("gitHubRestClient")
    private final RestClient restClient;

    public GitHubReleaseResponseDto getLatestRelease(final String repo) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CLIENT_URL)
                        .path("/{repo}/releases/latest")
                        .build(repo))
                .retrieve()
                .body(GitHubReleaseResponseDto.class);
    }
}
