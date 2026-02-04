package br.com.gorillaroxo.sanjy.client.web.config;

import br.com.gorillaroxo.sanjy.client.web.exception.InvalidValuesException;
import br.com.gorillaroxo.sanjy.client.web.validation.NoUnderscoreInHostname;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanjy-client-web", ignoreUnknownFields = false)
public record SanjyClientWebConfigProp(
        @NotNull @Valid ExternalApisProp externalHttpClients,
        @NotNull @Valid ApplicationProp application,
        @NotNull @Valid LoggingProp logging,
        @NotNull @Valid UploadProp upload,
        @NotNull @Valid AiProp ai) {

    public record AiProp(
            @NotNull @Valid AiGenericConfigProp openAI,
            @NotNull @Valid AiGenericConfigProp anthropic) {}

    @Getter(onMethod_ = @__(@JsonProperty))
    @Accessors(fluent = true)
    public static class AiGenericConfigProp {

        private static final Pattern ENV_VAR_PLACEHOLDER_PATTERN = Pattern.compile("^\\$\\{.*}$");

        private final String apiKey;
        private final String model;
        private final Integer maxTokens;
        private final Double temperature;
        private final List<String> stopSequences;
        private final Double topP;

        public AiGenericConfigProp(
                final String apiKey,
                final String model,
                final String maxTokens,
                final String temperature,
                final List<String> stopSequences,
                final String topP) {
            this.apiKey = apiKey;
            this.model = model;
            this.maxTokens = safelyConvertNumbers("SANJY_CLIENT_WEB_AI_MAX_TOKENS", maxTokens, Integer::parseInt);
            this.temperature =
                    safelyConvertNumbers("SANJY_CLIENT_WEB_AI_MAX_TEMPERATURE", temperature, Double::parseDouble);
            this.stopSequences = Objects.requireNonNullElse(stopSequences, new ArrayList<>());
            this.topP = safelyConvertNumbers("SANJY_CLIENT_WEB_AI_MAX_STOP_TOP_P", topP, Double::parseDouble);
        }

        private static <T> T safelyConvertNumbers(
                final String envName, final String value, Function<String, T> converter) {
            return Optional.ofNullable(value)
                    .filter(Predicate.not(String::isBlank))
                    .filter(Predicate.not(
                            val -> ENV_VAR_PLACEHOLDER_PATTERN.matcher(val).matches()))
                    .map(valuePresent -> {
                        try {
                            Double.parseDouble(valuePresent);
                        } catch (final NumberFormatException ignored) {
                            throw new InvalidValuesException("""
                                Invalid configuration: Environment Variable '%s' must be a valid number, but received '%s'
                                """.formatted(envName, valuePresent));
                        }
                        return converter.apply(valuePresent);
                    })
                    .orElse(null);
        }
    }

    public record ExternalApisProp(
            @Valid GenericApiProp sanjyServer, @Valid GenericApiProp github) {}

    public record GenericApiProp(
            @NotNull @URL @NoUnderscoreInHostname String url) {}

    public record ApplicationProp(
            @NotBlank String name,
            @NotBlank String version,
            @NotBlank String description,
            @NotNull @Valid ApplicationContactPropImpl contact,
            @NotNull @Valid ApplicationDocumentationPropImpl documentation,
            @NotBlank String channel) {}

    record ApplicationContactPropImpl(
            @NotBlank String name,
            @NotBlank @URL String url,
            @NotBlank @Email String email) {}

    record ApplicationDocumentationPropImpl(
            @NotBlank @URL String url, @NotBlank String description) {}

    public record LoggingProp(
            @NotBlank String level,
            @NotBlank String filePath,
            @NotBlank String appender) {}

    public record UploadProp(@NotNull Integer maxFileSizeInMb) {}
}
