package br.com.gorillaroxo.sanjy.client.web.config.ai;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.config.ai.provider.strategy.ChatModelProviderStrategy;
import br.com.gorillaroxo.sanjy.client.web.exception.AmbiguousAiProviderException;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChatModelWrapperConfig {

    private final SanjyClientWebConfigProp configProp;
    private final List<ChatModelProviderStrategy> modelProviderStrategies;

    @Bean
    public ChatModelWrapper chatModelProvider() {
        final List<ChatModelProviderStrategy> strategiesFound = modelProviderStrategies.stream()
            .filter(strategy -> strategy.accept(configProp.ai()))
            .toList();

        if (strategiesFound.isEmpty()) {
            log.info(
                LogField.Placeholders.ONE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "No AI Provider was configured. AI Features will be disabled"));

            return new ChatModelWrapper(null);
        }

        if (strategiesFound.size() > 1) {
            final String providerNames = strategiesFound.stream()
                .map(f -> f.providerName(configProp.ai()))
                .collect(Collectors.joining(", "));

            throw new AmbiguousAiProviderException("providers configured: " + providerNames);
        }

        return strategiesFound.getFirst().build(configProp.ai());
    }
}
