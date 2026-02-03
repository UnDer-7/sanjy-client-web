package br.com.gorillaroxo.sanjy.client.web.test;

import br.com.gorillaroxo.sanjy.client.web.SanjyClientWebApplication;
import br.com.gorillaroxo.sanjy.client.web.test.client.DietPlanRestClientMock;
import br.com.gorillaroxo.sanjy.client.web.test.mockwebserver.MockWebServerDispatcher;
import br.com.gorillaroxo.sanjy.client.web.test.mockwebserver.MockWebServerManager;
import br.com.gorillaroxo.sanjy.client.web.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Base class for integration tests with MockWebServer.
 *
 * <p>This class sets up MockWebServer to mock external HTTP endpoints (sanjy-server, GitHub)
 * without using Mockito, making it compatible with both JVM and GraalVM Native Image tests.
 *
 * <p>The GitHub API mock is pre-configured in {@link MockWebServerManager} to handle
 * the version check that occurs during application startup (ApplicationReadyEvent).
 */
@Slf4j
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = SanjyClientWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestController {

    private MockWebServerDispatcher dispatcher;

    protected DietPlanRestClientMock dietPlanRestClientMock;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected JsonUtil jsonUtil;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("mockwebserver.url", MockWebServerManager::getBaseUrl);
    }

    /**
     * Initializes MockWebServer and mock clients.
     * This method is non-static because we use {@code @TestInstance(Lifecycle.PER_CLASS)},
     * which allows access to Spring-injected beans like {@link JsonUtil}.
     */
    @BeforeAll
    void initMockWebServer() {
        final MockWebServer mockWebServer = MockWebServerManager.getInstance();
        dispatcher = new MockWebServerDispatcher();
        MockWebServerManager.setDispatcher(dispatcher);
        dietPlanRestClientMock = new DietPlanRestClientMock(mockWebServer, dispatcher, jsonUtil);
    }

    @BeforeEach
    void setUp() {
        dispatcher.reset();
    }
}
