package br.com.gorillaroxo.sanjy.client.web.config.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * Provider for the optional ChatModel.
 * This class wraps the ChatModel which may be null if no AI provider is configured.
 * Use this to safely access AI features without causing startup failures.
 */
@RequiredArgsConstructor
public class ChatModelWrapper {

    @Nullable
    private final ChatModel chatModel;

    /**
     * Returns the ChatModel wrapped in an Optional.
     *
     * @return Optional containing the ChatModel if configured, empty otherwise
     */
    public Optional<ChatModel> getChatModel() {
        return Optional.ofNullable(chatModel);
    }

    /**
     * Returns a ChatClient.Builder wrapped in an Optional.
     * Use this when you need to build a customized ChatClient.
     *
     * @return Optional containing a ChatClient.Builder if AI is configured, empty otherwise
     */
    public Optional<ChatClient.Builder> getChatClientBuilder() {
        return getChatModel().map(model -> ChatClient.create(model).mutate());
    }

}
