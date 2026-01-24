package br.com.gorillaroxo.sanjy.client.web.config;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.UUIDSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final SanjyClientWebConfigProp prop;

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.setInfo(buildInfo(openApi));
            openApi.setExternalDocs(buildExternalDocs(openApi));
        };
    }

    @Bean
    public OperationCustomizer globalHeadersCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            final Parameter correlationIdParam = new Parameter()
                    .in("header")
                    .name(RequestConstants.Headers.X_CORRELATION_ID)
                    .description(
                            "Unique identifier for tracking the request across services. Must be a valid UUID format.")
                    .required(true)
                    .schema(new UUIDSchema())
                    .example("550e8400-e29b-41d4-a716-446655440000");

            operation.addParametersItem(correlationIdParam);
            return operation;
        };
    }

    private ExternalDocumentation buildExternalDocs(final OpenAPI openApi) {
        final var externalDocs = Objects.requireNonNullElseGet(openApi.getExternalDocs(), ExternalDocumentation::new);

        final var documentation = prop.application().documentation();
        externalDocs.setUrl(documentation.url());
        externalDocs.setDescription(documentation.description());

        return externalDocs;
    }

    private Info buildInfo(final OpenAPI openApi) {
        final var info = Objects.requireNonNullElseGet(openApi.getInfo(), Info::new);
        final var applicationProp = prop.application();
        info.setTitle(applicationProp.name());
        info.description(applicationProp.description());
        info.version(applicationProp.version());

        final var contact = Objects.requireNonNullElseGet(openApi.getInfo().getContact(), Contact::new);
        final var contactProp = applicationProp.contact();
        contact.setName(contactProp.name());
        contact.setUrl(contactProp.url());
        contact.setEmail(contactProp.email());

        info.setContact(contact);

        return info;
    }
}
