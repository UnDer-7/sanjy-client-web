package br.com.gorillaroxo.sanjy.client.web.config.ai.provider.strategy;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.config.ai.ChatModelWrapper;

public interface ChatModelProviderStrategy {

    boolean accept(SanjyClientWebConfigProp.AiProp aiProp);

    ChatModelWrapper build(SanjyClientWebConfigProp.AiProp aiProp);

    String providerName(SanjyClientWebConfigProp.AiProp aiProp);

}
