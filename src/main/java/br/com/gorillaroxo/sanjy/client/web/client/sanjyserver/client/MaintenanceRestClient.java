package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.client;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.ProjectInfoResponseDto;
import br.com.gorillaroxo.sanjy.client.web.exception.UnhandledClientHttpException;
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

    /**
     * Retrieves project information including version details (current and latest),
     * application and database timezone configuration, and the current runtime mode.
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    public ProjectInfoResponseDto projectInfo() {
        return restClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(CLIENT_URL).path("/project-info").build())
                .retrieve()
                .body(ProjectInfoResponseDto.class);
    }
}
