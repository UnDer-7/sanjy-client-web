package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import lombok.Builder;

@Builder
public record FrontendRuntimeConfigurationControllerResponseDto(String logoutUrl) {}
