package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.config.ai.ChatModelWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAvailabilityService {

    private final ChatModelWrapper chatModelWrapper;

    public boolean execute() {
        return chatModelWrapper.isAvailable();
    }
}
