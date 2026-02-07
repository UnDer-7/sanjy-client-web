package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.client;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.ProjectInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceRestClient {

    private static final String CLIENT_URL = "/v1/maintenance";

    @Qualifier("sanjyServerRestClient")
    private final RestClient restClient;

    public ProjectInfoResponseDto projectInfo() {
        return restClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(CLIENT_URL).path("/project-info").build())
            .retrieve()
            .body(ProjectInfoResponseDto.class);
    }
}
