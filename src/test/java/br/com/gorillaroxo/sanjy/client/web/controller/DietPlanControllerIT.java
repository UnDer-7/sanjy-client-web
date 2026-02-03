package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.test.IntegrationTestController;
import br.com.gorillaroxo.sanjy.client.web.test.builder.DtoBuilders;
import br.com.gorillaroxo.sanjy.client.web.test.builder.DtoControllerBuilders;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@Slf4j
class DietPlanControllerIT extends IntegrationTestController {

    private static final String RESOURCE_URL = "/api/v1/diet-plan";

    @Test
    void should_create_diet_plan() {
        final var uuid = UUID.randomUUID().toString();
        final var requestBody = DtoControllerBuilders.buildDietPlanControllerRequestDto().build();
        final var responseBody = DtoBuilders.buildDietPlanResponseDto().build();

        log.info("INTEGRATION TEST create diet plan");
        dietPlanRestClientMock.newDietPlan().success(uuid, jsonUtil.serialize(responseBody));

        webTestClient
                .post()
                .uri(RESOURCE_URL)
                .header(RequestConstants.Headers.X_CORRELATION_ID, uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}
