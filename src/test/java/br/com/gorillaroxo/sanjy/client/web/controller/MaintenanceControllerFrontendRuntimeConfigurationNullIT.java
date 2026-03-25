package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.FrontendRuntimeConfigurationControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestPropertySource(properties = "sanjy-client-web.frontend-runtime-configuration.logout-url.value=")
class MaintenanceControllerFrontendRuntimeConfigurationNullIT extends IntegrationTestController {

    static final String RESOURCE_URL = "/api/v1/maintenance";

    @Nested
    @DisplayName("GET /api/v1/maintenance/frontend-runtime-configuration")
    class FrontendRuntimeConfiguration {

        private static final String FRONTEND_RUNTIME_CONFIGURATION_URL =
                RESOURCE_URL + "/frontend-runtime-configuration";

        @Test
        @DisplayName("Should return null logoutUrl when not configured")
        void should_return_null_logout_url_when_not_configured() {
            final var uuid = UUID.randomUUID().toString();

            webTestClient
                    .get()
                    .uri(FRONTEND_RUNTIME_CONFIGURATION_URL)
                    .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(FrontendRuntimeConfigurationControllerResponseDto.class)
                    .value(actual -> {
                        assertThat(actual.logoutUrl()).isNotNull();
                        assertThat(actual.logoutUrl().value()).isNull();
                        assertThat(actual.logoutUrl().env())
                                .isEqualTo("SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL");
                    });
        }
    }
}
