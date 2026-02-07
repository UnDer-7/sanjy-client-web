package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import lombok.Builder;

@Builder
public record ProjectInfoMaintenanceControllerResponseDto(
    Project sanjyClientWeb,
    Project sanjyServer
) {

    @Builder
    public record Project(
        Version version,
        String runtimeMode
    ) {
    }

    @Builder
    public record Version(
        String current,
        String latest,
        Boolean isLatest
    ) {}
}
