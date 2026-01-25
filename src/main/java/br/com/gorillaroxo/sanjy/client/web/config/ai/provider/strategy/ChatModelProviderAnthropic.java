package br.com.gorillaroxo.sanjy.client.web.config.ai.provider.strategy;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.config.ai.ChatModelWrapper;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class ChatModelProviderAnthropic implements ChatModelProviderStrategy {

    @Override
    public boolean accept(final SanjyClientWebConfigProp.AiProp aiProp) {
        final SanjyClientWebConfigProp.AiGenericConfigProp config = aiProp.anthropic();

        return config.apiKey() != null && !config.apiKey().isBlank();
    }

    @Override
    public ChatModelWrapper build(final SanjyClientWebConfigProp.AiProp aiProp) {
        final SanjyClientWebConfigProp.AiGenericConfigProp config = aiProp.anthropic();

        log.info(
                LogField.Placeholders.SIX.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Anthropic selected as AI Provider"),
                StructuredArguments.kv(LogField.AI_MODEL.label(), config.model()),
                StructuredArguments.kv(LogField.AI_MAX_TOKENS.label(), config.maxTokens()),
                StructuredArguments.kv(LogField.AI_TEMPERTURE.label(), config.temperature()),
                StructuredArguments.kv(LogField.AI_STOP_SEQUENCES.label(), config.stopSequences()),
                StructuredArguments.kv(LogField.AI_TOP_P.label(), config.topP()));

        final AnthropicApi apiKey = AnthropicApi.builder()
                .apiKey(Objects.requireNonNull(config.apiKey(), "Anthropic ApiKey cannot be null"))
                .build();
        final AnthropicChatOptions anthropicOptions = AnthropicChatOptions.builder()
                .model(Objects.requireNonNull(config.model()))
                .maxTokens(Objects.requireNonNull(config.maxTokens()))
                .temperature(Objects.requireNonNull(config.temperature()))
                .stopSequences(Objects.requireNonNull(config.stopSequences()))
                .topP(Objects.requireNonNull(config.topP()))
                .build();
        var chatModel = AnthropicChatModel.builder()
                .anthropicApi(apiKey)
                .defaultOptions(anthropicOptions)
                .build();
        return new ChatModelWrapper(chatModel);
    }

    @Override
    public String providerName(final SanjyClientWebConfigProp.AiProp aiProp) {
        return "Anthropic";
    }
}
