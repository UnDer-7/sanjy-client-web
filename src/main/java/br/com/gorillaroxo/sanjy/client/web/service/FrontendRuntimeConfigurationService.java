package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.FrontendRuntimeConfigurationControllerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FrontendRuntimeConfigurationService {

    private final SanjyClientWebConfigProp prop;

    public FrontendRuntimeConfigurationControllerResponseDto execute() {
        final var frontendConfig = prop.frontendRuntimeConfiguration();
        final var logoutUrlProp = frontendConfig.logoutUrl();
        final var appTitleRedirectPathProp = frontendConfig.appTitleRedirectPath();
        return FrontendRuntimeConfigurationControllerResponseDto.builder()
                .logoutUrl(new FrontendRuntimeConfigurationControllerResponseDto.RuntimeConfigEntryDto(
                        logoutUrlProp.envName(), logoutUrlProp.value()))
                .appTitleRedirectPath(new FrontendRuntimeConfigurationControllerResponseDto.RuntimeConfigEntryDto(
                        appTitleRedirectPathProp.envName(), appTitleRedirectPathProp.value()))
                .build();
    }
}
