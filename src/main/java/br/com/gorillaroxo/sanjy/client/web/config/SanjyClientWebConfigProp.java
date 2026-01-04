package br.com.gorillaroxo.sanjy.client.web.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanjy-client-web",  ignoreUnknownFields = false)
public record SanjyClientWebConfigProp(
    @NotNull @Valid ExternalApisProp externalHttpClients,
    @NotNull @Valid ApplicationProp application,
    @NotNull @Valid LoggingProp logging,
    @NotNull @Valid UploadProp upload
) {

    public record ExternalApisProp(
        @NotNull @Valid HttpRetryProp httpRetry,
        @Valid GenericApiProp sanjyServer,
        @Valid GenericApiProp github
    ) {

    }

    public record GenericApiProp(
        @NotNull @URL String url
    ) {

    }

    public record ApplicationProp(
        @NotBlank String name,
        @NotBlank String version,
        @NotBlank String description,
        @NotNull @Valid ApplicationContactPropImpl contact,
        @NotNull @Valid ApplicationDocumentationPropImpl documentation,
        @NotBlank String channel
    ) {

    }

    record ApplicationContactPropImpl(
        @NotBlank String name,
        @NotBlank @URL String url,
        @NotBlank @Email String email) {}

    record ApplicationDocumentationPropImpl(
        @NotBlank @URL String url, @NotBlank String description) {}

    public record LoggingProp(
        @NotBlank String level,
        @NotBlank String filePath,
        @NotBlank String appender
    ) {
    }

    public record UploadProp(
        @NotNull Integer maxFileSizeInMb
    ) {
    }

    public record HttpRetryProp(
        @NotNull Integer maxAttempt,
        @NotNull Integer interval,
        @NotNull Integer backoffMultiplier
    ) {
    }
}
