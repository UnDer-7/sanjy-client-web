package br.com.gorillaroxo.sanjy.client.web.test;

import static org.mockito.Mockito.lenient;

import br.com.gorillaroxo.sanjy.client.web.SanjyClientWebApplication;
import br.com.gorillaroxo.sanjy.client.web.service.GetLatestProjectVersionService;
import br.com.gorillaroxo.sanjy.client.web.util.JsonUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

@Slf4j
@EnableWireMock
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = SanjyClientWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestController {

    @InjectWireMock
    protected WireMockServer wireMockServer;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected JsonUtil jsonUtil;

    /**
     * MockitoBean is required here instead of WireMock because ProjectInfoLoggerConfig
     * calls this service on ApplicationReadyEvent (during Spring context startup).
     * At that point, the WireMock stubs haven't been registered yet (they're set up in test methods),
     * causing a race condition where the GitHub endpoint returns 404.
     * Using MockitoBean ensures the service is mocked before the context finishes loading.
     */
    @MockitoBean
    protected GetLatestProjectVersionService getLatestProjectVersionService;

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
        lenient().when(getLatestProjectVersionService.clientWeb()).thenReturn("v1.0.0-test");
    }

}
