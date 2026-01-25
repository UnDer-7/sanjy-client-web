package br.com.gorillaroxo.sanjy.client.web.config.ai.provider.strategy;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.config.ai.ChatModelWrapper;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class ChatModelProviderOpenAi implements ChatModelProviderStrategy {

    @Override
    public boolean accept(final SanjyClientWebConfigProp.AiProp aiProp) {
        final SanjyClientWebConfigProp.AiGenericConfigProp config = aiProp.openAI();

        return config.apiKey() != null && !config.apiKey().isBlank();
    }

    @Override
    public ChatModelWrapper build(final SanjyClientWebConfigProp.AiProp aiProp) {
        final SanjyClientWebConfigProp.AiGenericConfigProp config = aiProp.openAI();

        log.info(
                LogField.Placeholders.SIX.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "OpenAI selected as AI Provider"),
                StructuredArguments.kv(LogField.AI_MODEL.label(), config.model()),
                StructuredArguments.kv(LogField.AI_MAX_TOKENS.label(), config.maxTokens()),
                StructuredArguments.kv(LogField.AI_TEMPERTURE.label(), config.temperature()),
                StructuredArguments.kv(LogField.AI_STOP_SEQUENCES.label(), config.stopSequences()),
                StructuredArguments.kv(LogField.AI_TOP_P.label(), config.topP()));

        final OpenAiApi openAi = OpenAiApi.builder()
                .apiKey(Objects.requireNonNull(config.apiKey()))
                .build();
        final OpenAiChatOptions openAiOptions = OpenAiChatOptions.builder()
                .model(Objects.requireNonNull(config.model()))
                .maxTokens(config.maxTokens())
                .temperature(config.temperature())
                .stop(config.stopSequences())
                .topP(config.topP())
                .build();
        final OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAi)
                .defaultOptions(openAiOptions)
                .build();

        return new ChatModelWrapper(chatModel);
    }

    @Override
    public String providerName(final SanjyClientWebConfigProp.AiProp aiProp) {
        return "OpenAi";
    }
}
