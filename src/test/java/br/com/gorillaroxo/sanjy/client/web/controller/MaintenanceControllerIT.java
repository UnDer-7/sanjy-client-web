package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.test.IntegrationTestController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

@Slf4j
class MaintenanceControllerIT extends IntegrationTestController {

    static final String RESOURCE_URL = "/api/v1/maintenance";


    @Nested
    @DisplayName("GET /api/v1/maintenance/project-info")
    class ProjectInfo {

    }

    @Nested
    @DisplayName("GET /api/v1/maintenance/ai/availability")
    class IsAvailable {

    }
}