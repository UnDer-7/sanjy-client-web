package br.com.gorillaroxo.sanjy.client.web.service.converter;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.DietPlanControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.exception.AiModelIntegrationFailureException;
import br.com.gorillaroxo.sanjy.client.web.exception.DietPlanConversionFailureException;
import br.com.gorillaroxo.sanjy.client.web.exception.NoAiProviderAvailableException;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service that converts text-based meal plans into structured DietPlanRequestDto using AI. If no AI provider is
 * configured, the convert method will return an empty Optional.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DietPlanConverter {

    @Qualifier("dietPlanConverterChatClient")
    private final Optional<ChatClient> chatClient;

    /**
     * Converts the input message (meal plan text) to a DietPlanRequestDto using AI.
     *
     * @param inputMessage the meal plan text to convert
     * @return Optional containing the converted DTO, or empty if conversion fails or AI is not available
     */
    public DietPlanControllerRequestDto convert(final String inputMessage) {
        final DietPlanControllerRequestDto entity = aiExtractor(inputMessage);

        if (entity == null || entity.isEmpty()) {
            log.warn(
                    LogField.Placeholders.TWO.getPlaceholder(),
                    StructuredArguments.kv(
                            LogField.MSG.label(),
                            "Could not convert inputMessage into Diet Plan class, A.I. model return null"),
                    StructuredArguments.kv(LogField.INPUT_MESSAGE.label(), inputMessage));

            throw new DietPlanConversionFailureException();
        }

        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(
                        LogField.MSG.label(), "Successfully converted inputMessage to Diet Plan class using A.I."));

        return entity;
    }

    private DietPlanControllerRequestDto aiExtractor(final String inputMessage) {
        if (chatClient.isEmpty()) {
            log.warn(
                    LogField.Placeholders.ONE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), """
                        AI conversion not available - no AI provider configured. Set some API Key Environment Variable to enable it.
                        """));
            throw new NoAiProviderAvailableException();
        }

        try {
            log.info(
                    LogField.Placeholders.ONE.getPlaceholder(),
                    StructuredArguments.kv(
                            LogField.MSG.label(), "Converting inputMessage to Diet Plan class using A.I."));

            return chatClient.get().prompt().user(inputMessage).call().entity(DietPlanControllerRequestDto.class);
        } catch (final Exception e) {
            log.warn(
                    LogField.Placeholders.TWO.getPlaceholder(),
                    StructuredArguments.kv(
                            LogField.MSG.label(),
                            "An error occurred during converting inputMessage into Diet Plan class using A.I."),
                    StructuredArguments.kv(LogField.INPUT_MESSAGE.label(), inputMessage),
                    e);

            throw new AiModelIntegrationFailureException("Error calling AI model: " + e.getMessage(), e);
        }
    }
}
