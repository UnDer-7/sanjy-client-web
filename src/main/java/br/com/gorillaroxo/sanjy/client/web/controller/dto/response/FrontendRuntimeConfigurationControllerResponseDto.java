package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import lombok.Builder;

@Builder
public record FrontendRuntimeConfigurationControllerResponseDto(RuntimeConfigEntryDto logoutUrl) {

    public record RuntimeConfigEntryDto(String env, String value) {}
}
