package br.com.gorillaroxo.sanjy.client.web.test.mockwebserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.RecordedRequest;
import org.jetbrains.annotations.NotNull;

/**
 * A flexible dispatcher for MockWebServer that allows registering handlers for specific paths. This enables
 * WireMock-like stubbing behavior where responses are matched by URL path rather than using a queue (FIFO) approach.
 */
@Slf4j
public class MockWebServerDispatcher extends Dispatcher {

    private final Map<String, Function<RecordedRequest, MockResponse>> handlers = new ConcurrentHashMap<>();

    /**
     * Registers a handler for a specific path.
     *
     * @param path The URL path to match (e.g., "/sanjy-server/v1/diet-plan")
     * @param handler A function that takes the RecordedRequest and returns a MockResponse
     */
    public void register(String path, Function<RecordedRequest, MockResponse> handler) {
        handlers.put(path, handler);
        log.debug("Registered mock handler for path: {}", path);
    }

    /**
     * Removes a handler for a specific path.
     *
     * @param path The URL path to remove
     */
    public void unregister(String path) {
        handlers.remove(path);
        log.debug("Unregistered mock handler for path: {}", path);
    }

    /** Clears all registered handlers. */
    public void reset() {
        handlers.clear();
        log.debug("Reset all mock handlers");
    }

    @NotNull
    @Override
    public MockResponse dispatch(@NotNull RecordedRequest request) {
        String path = request.getPath();
        log.debug("MockWebServer received request: {} {}", request.getMethod(), path);

        if (path == null) {
            return new MockResponse.Builder().code(404).body("Path is null").build();
        }

        // Try exact match first
        Function<RecordedRequest, MockResponse> handler = handlers.get(path);

        // If no exact match, try matching without query parameters
        if (handler == null) {
            String pathWithoutQuery = path.contains("?") ? path.substring(0, path.indexOf("?")) : path;
            handler = handlers.get(pathWithoutQuery);
        }

        if (handler != null) {
            MockResponse response = handler.apply(request);
            log.debug("MockWebServer returning response with code: {}", response.getCode());
            return response;
        }

        log.warn("No handler registered for path: {}", path);
        return new MockResponse.Builder()
                .code(404)
                .body("No handler registered for path: " + path)
                .build();
    }
}
