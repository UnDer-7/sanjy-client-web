package br.com.gorillaroxo.sanjy.client.web.test;

import br.com.gorillaroxo.sanjy.client.web.SanjyClientWebApplication;
import br.com.gorillaroxo.sanjy.client.web.test.ai.FakeChatModel;
import br.com.gorillaroxo.sanjy.client.web.test.ai.TestChatModelConfig;
import br.com.gorillaroxo.sanjy.client.web.test.client.DietPlanRestClientMock;
import br.com.gorillaroxo.sanjy.client.web.test.client.MaintenanceRestClientMock;
import br.com.gorillaroxo.sanjy.client.web.test.client.MealRecordRestClientMock;
import br.com.gorillaroxo.sanjy.client.web.test.mockwebserver.MockWebServerDispatcher;
import br.com.gorillaroxo.sanjy.client.web.test.mockwebserver.MockWebServerManager;
import br.com.gorillaroxo.sanjy.client.web.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Base class for integration tests with MockWebServer.
 *
 * <p>This class sets up MockWebServer to mock external HTTP endpoints (sanjy-server, GitHub) without using Mockito,
 * making it compatible with both JVM and GraalVM Native Image tests.
 *
 * <p>The GitHub API mock is pre-configured in {@link MockWebServerManager} to handle the version check that occurs
 * during application startup (ApplicationReadyEvent).
 */
@Slf4j
@ActiveProfiles("test")
@Import(TestChatModelConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = SanjyClientWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestController {

    private MockWebServerDispatcher dispatcher;

    protected DietPlanRestClientMock dietPlanRestClientMock;

    protected MaintenanceRestClientMock maintenanceRestClientMock;

    protected MealRecordRestClientMock mealRecordRestClientMock;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected JsonUtil jsonUtil;

    @Nullable
    @Autowired(required = false)
    protected FakeChatModel fakeChatModel;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("mockwebserver.url", MockWebServerManager::getBASE_URL);
    }

    /**
     * Initializes MockWebServer and mock clients. This method is non-static because we use
     * {@code @TestInstance(Lifecycle.PER_CLASS)}, which allows access to Spring-injected beans like {@link JsonUtil}.
     */
    @BeforeAll
    void initMockWebServer() {
        dispatcher = new MockWebServerDispatcher();
        MockWebServerManager.setDispatcher(dispatcher);
        dietPlanRestClientMock = new DietPlanRestClientMock(dispatcher, jsonUtil);
        maintenanceRestClientMock = new MaintenanceRestClientMock(dispatcher, jsonUtil);
        mealRecordRestClientMock = new MealRecordRestClientMock(dispatcher, jsonUtil);
    }

    @BeforeEach
    void setUp() {
        dispatcher.reset();
        if (fakeChatModel != null) {
            fakeChatModel.reset();
        }
    }
}
