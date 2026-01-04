package br.com.gorillaroxo.sanjy.client.web.client.github;

import br.com.gorillaroxo.sanjy.client.web.client.github.dto.response.GitHubReleaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        value = "GitHubReposFeignClient",
        url = "${sanjy-client-web.external-http-clients.github.url}",
        path = "/repos/UnDer-7")
public interface GitHubReleaseFeignClient {

    @GetMapping("/{repo}/releases/latest")
    GitHubReleaseResponseDto getLatestRelease(@PathVariable("repo") String repo);
}
