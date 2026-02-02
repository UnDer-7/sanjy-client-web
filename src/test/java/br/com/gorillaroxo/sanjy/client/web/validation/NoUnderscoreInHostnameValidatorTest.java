package br.com.gorillaroxo.sanjy.client.web.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoUnderscoreInHostnameValidatorTest {

    @InjectMocks
    private NoUnderscoreInHostnameValidator validator;

    @Nested
    @DisplayName("Valid URLs (should return true)")
    class ValidUrls {

        @NullAndEmptySource
        @ParameterizedTest(name = "URL: {0}")
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should return true for null, empty or blank URLs")
        void shouldReturnTrueForNullEmptyOrBlankUrls(String url) {
            assertThat(validator.isValid(url, null)).isTrue();
        }

        @ParameterizedTest(name = "URL: {0}")
        @MethodSource("br.com.gorillaroxo.sanjy.client.web.validation.NoUnderscoreInHostnameValidatorTest#validUrlsProvider")
        @DisplayName("Should return true for URLs with valid hostnames (no underscores)")
        void shouldReturnTrueForValidHostnames(String url, String description) {
            assertThat(validator.isValid(url, null))
                    .as("URL '%s' (%s) should be valid", url, description)
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("Invalid URLs (should return false)")
    class InvalidUrls {

        @ParameterizedTest(name = "URL: {0}")
        @MethodSource("br.com.gorillaroxo.sanjy.client.web.validation.NoUnderscoreInHostnameValidatorTest#invalidUrlsProvider")
        @DisplayName("Should return false for URLs with underscores in hostname")
        void shouldReturnFalseForHostnamesWithUnderscore(String url, String description) {
            assertThat(validator.isValid(url, null))
                    .as("URL '%s' (%s) should be invalid", url, description)
                    .isFalse();
        }
    }

    static Stream<Arguments> validUrlsProvider() {
        return Stream.of(
                // Standard URLs with hyphens
                Arguments.of("http://sanjy-server:8080", "hyphen in hostname"),
                Arguments.of("https://my-service:443", "HTTPS with hyphen"),
                Arguments.of("http://my-app-server:8080/api", "hyphen with path"),

                // Localhost and IP addresses
                Arguments.of("http://localhost:8080", "localhost"),
                Arguments.of("http://127.0.0.1:8080", "IPv4 address"),
                Arguments.of("http://192.168.1.100:3000", "private IPv4"),

                // Standard domains
                Arguments.of("https://api.github.com", "public domain"),
                Arguments.of("https://www.google.com:443/search", "domain with path"),
                Arguments.of("http://sub.domain.example.com:8080", "subdomain"),

                // With authentication
                Arguments.of("http://user:pass@my-server:8080", "with basic auth"),
                Arguments.of("https://admin:secret@api-gateway:443/v1", "auth with path"),
                Arguments.of("http://user_name:pass_word@valid-host:8080", "underscore in auth is OK"),

                // Underscore in path is OK (only hostname matters)
                Arguments.of("http://sanjy-server:8080/api_v1", "underscore in path"),
                Arguments.of("http://localhost:8080/some_endpoint/with_underscore", "multiple underscores in path"),
                Arguments.of("https://api.github.com/repos/user_name/repo_name", "underscore in path segments"),
                Arguments.of("http://my-server:8080/api?param_name=value_here", "underscore in query params"),

                // Edge cases
                Arguments.of("http://a:8080", "single char hostname"),
                Arguments.of("http://server123:8080", "hostname with numbers"),
                Arguments.of("ftp://ftp-server:21", "FTP protocol"),

                // URLs that don't match pattern (let @URL handle them)
                Arguments.of("not-a-url", "invalid URL format"),
                Arguments.of("://missing-scheme", "missing scheme"));
    }

    static Stream<Arguments> invalidUrlsProvider() {
        return Stream.of(
                // Docker container names with underscores
                Arguments.of("http://sanjy_server:8080", "Docker container with underscore"),
                Arguments.of("http://my_service:3000", "service with underscore"),
                Arguments.of("https://app_backend:443", "HTTPS with underscore"),

                // Multiple underscores
                Arguments.of("http://my_app_server:8080", "multiple underscores"),
                Arguments.of("http://sanjy_client_web:8081", "three parts with underscores"),

                // Underscore at different positions
                Arguments.of("http://_server:8080", "underscore at start"),
                Arguments.of("http://server_:8080", "underscore at end"),
                Arguments.of("http://my__server:8080", "double underscore"),

                // With path and query
                Arguments.of("http://my_server:8080/api/v1", "underscore with path"),
                Arguments.of("http://my_server:8080/api?query=1", "underscore with query"),

                // With authentication (underscore in hostname, not in auth)
                Arguments.of("http://user:pass@my_server:8080", "underscore with auth"),
                Arguments.of("https://admin:secret@backend_service:443/api", "underscore with auth and path"),

                // Different protocols
                Arguments.of("https://my_secure_server:443", "HTTPS with underscore"),
                Arguments.of("ftp://ftp_server:21", "FTP with underscore"));
    }
}
