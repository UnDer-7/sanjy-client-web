package br.com.gorillaroxo.sanjy.client.web.test.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration that exposes the FakeChatModel from ChatModelProviderStrategyTest.
 *
 * <p>The FakeChatModel is created by ChatModelProviderStrategyTest which is a @Service
 * that gets picked up by ChatModelWrapperConfig during bean creation.
 */
@Slf4j
@TestConfiguration
@RequiredArgsConstructor
public class TestChatModelConfig {

    private final ChatModelProviderStrategyMock chatModelProviderStrategyMock;

    /**
     * Returns the FakeChatModel instance for test manipulation.
     */
    @Bean
    public FakeChatModel fakeChatModel() {
        return chatModelProviderStrategyMock.getFakeChatModel();
    }
}
