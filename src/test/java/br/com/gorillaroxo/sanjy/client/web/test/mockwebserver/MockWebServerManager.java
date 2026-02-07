package br.com.gorillaroxo.sanjy.client.web.test.mockwebserver;

import java.io.IOException;
import lombok.Getter;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Singleton manager for MockWebServer instance. Ensures the same server instance is shared across all tests in the same
 * JVM, avoiding port conflicts and providing a stable base URL for dynamic property injection.
 *
 * <p>The server is started eagerly in a static block to ensure the base URL is available when
 * {@code @DynamicPropertySource} methods are called during Spring context initialization.
 *
 * <p>A default dispatcher is configured to handle GitHub API calls during application startup (called by
 * ProjectInfoLoggerConfig on ApplicationReadyEvent).
 */
public final class MockWebServerManager {

    @Getter
    private static final MockWebServer instance;

    @Getter
    private static final String BASE_URL;

    static {
        try {
            instance = new MockWebServer();
            // Set a default dispatcher that handles GitHub API calls during startup
            instance.setDispatcher(new DefaultStartupDispatcher());
            instance.start();
            String url = instance.url("/").toString();
            // Remove trailing slash for consistency
            BASE_URL = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to start MockWebServer: " + e.getMessage());
        }
    }

    private MockWebServerManager() {}

    public static void setDispatcher(MockWebServerDispatcher dispatcher) {
        instance.setDispatcher(new DelegatingDispatcher(dispatcher));
    }

    public static void shutdown() throws IOException {
        instance.shutdown();
    }

    /**
     * Default dispatcher that handles requests during Spring context startup. Returns mock responses for GitHub API
     * calls that happen before test methods configure mocks.
     */
    private static class DefaultStartupDispatcher extends Dispatcher {
        @NotNull
        @Override
        public MockResponse dispatch(@NotNull RecordedRequest request) {
            String path = request.getPath();
            if (path != null && path.contains("/github/repos/") && path.contains("/releases/latest")) {
                return new MockResponse.Builder()
                        .code(200)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("{\"tag_name\": \"v1.0.0-test\"}")
                        .build();
            }
            return new MockResponse.Builder()
                    .code(404)
                    .body("No handler for: " + path)
                    .build();
        }
    }

    /** Dispatcher that delegates to the test's dispatcher but falls back to GitHub mock for startup requests. */
    private static class DelegatingDispatcher extends Dispatcher {
        private final MockWebServerDispatcher delegate;

        DelegatingDispatcher(MockWebServerDispatcher delegate) {
            this.delegate = delegate;
        }

        @NotNull
        @Override
        public MockResponse dispatch(@NotNull RecordedRequest request) {
            MockResponse response = delegate.dispatch(request);
            // If the delegate returns 404 and it's a GitHub API call, handle it
            if (response.getCode() == 404) {
                String path = request.getPath();
                if (path != null && path.contains("/github/repos/") && path.contains("/releases/latest")) {
                    return new MockResponse.Builder()
                            .code(200)
                            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body("{\"tag_name\": \"v1.0.0-test\"}")
                            .build();
                }
            }
            return response;
        }
    }
}
