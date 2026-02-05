package br.com.gorillaroxo.sanjy.client.web.test.ai;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.config.ai.ChatModelWrapper;
import br.com.gorillaroxo.sanjy.client.web.config.ai.provider.strategy.ChatModelProviderStrategy;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * Test implementation of ChatModelProviderStrategy that provides a FakeChatModel. This strategy has highest priority
 * (@Order(0)) and is always accepted in test profile.
 *
 * <p>Since this is a @Service in the test classpath with @Profile("test"), it will be discovered by Spring's component
 * scan and used by ChatModelWrapperConfig.
 */
@Slf4j
@Service
@Order(0) // Highest priority - will be evaluated first
@Profile("test")
public class ChatModelProviderStrategyMock implements ChatModelProviderStrategy {

    @Getter
    private final FakeChatModel fakeChatModel = new FakeChatModel();

    @Override
    public boolean accept(final SanjyClientWebConfigProp.AiProp aiProp) {
        // Always accept in test profile
        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(
                        LogField.MSG.label(), "ChatModelProviderStrategyTest accepting - using FakeChatModel"));

        return true;
    }

    @Override
    public ChatModelWrapper build(final SanjyClientWebConfigProp.AiProp aiProp) {
        log.info("ChatModelProviderStrategyTest building ChatModelWrapper with FakeChatModel");
        return new ChatModelWrapper(fakeChatModel);
    }

    @Override
    public String providerName(final SanjyClientWebConfigProp.AiProp aiProp) {
        return "MockFakeChatModel";
    }
}
