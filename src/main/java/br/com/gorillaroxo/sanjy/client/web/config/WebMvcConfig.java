package br.com.gorillaroxo.sanjy.client.web.config;

import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

@Slf4j
@Configuration
@RestController
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final SanjyClientWebConfigProp configProp;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        Optional.ofNullable(configProp.cors())
                .map(SanjyClientWebConfigProp.CorsProp::allowedOrigins)
                .filter(Predicate.not(String::isBlank))
                .ifPresent(origins -> {
                    final String[] corsOrigins = origins.split(",");

                    log.info(
                        LogField.Placeholders.TWO.getPlaceholder(),
                        StructuredArguments.kv(LogField.MSG.label(), "Allowing CORS"),
                        StructuredArguments.kv(LogField.CORS_ORIGIN_PATTERNS.label(), "( " + String.join(", ", corsOrigins) + " )"));

                    registry.addMapping("/api/**").allowedOriginPatterns(corsOrigins);
                });
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        serveDirectory(registry, "/", "classpath:/static/");
    }

    private static void serveDirectory(ResourceHandlerRegistry registry, String endpoint, String location) {
        String[] endpointPatterns = endpoint.endsWith("/")
                ? new String[] {endpoint.substring(0, endpoint.length() - 1), endpoint, endpoint + "**"}
                : new String[] {endpoint, endpoint + "/", endpoint + "/**"};

        registry.addResourceHandler(endpointPatterns)
                .addResourceLocations(location.endsWith("/") ? location : location + "/")
                .resourceChain(false)
                .addResolver(new PathResourceResolver() {
                    @Override
                    public Resource resolveResource(
                            HttpServletRequest request,
                            String requestPath,
                            List<? extends Resource> locations,
                            ResourceResolverChain chain) {
                        Resource resource = super.resolveResource(request, requestPath, locations, chain);
                        if (Objects.nonNull(resource)) {
                            return resource;
                        }
                        return super.resolveResource(request, "/index.html", locations, chain);
                    }
                });
    }
}
