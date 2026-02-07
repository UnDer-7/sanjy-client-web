package br.com.gorillaroxo.sanjy.client.web.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.com.gorillaroxo.sanjy.client.web.config.ai.ChatModelWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AiAvailabilityServiceTest {

    @Mock
    private ChatModelWrapper chatModelWrapper;

    @InjectMocks
    private AiAvailabilityService aiAvailabilityService;

    @Test
    void execute_whenAiIsAvailable_shouldReturnTrue() {
        when(chatModelWrapper.isAvailable()).thenReturn(true);

        boolean result = aiAvailabilityService.execute();

        assertThat(result).isTrue();
    }

    @Test
    void execute_whenAiIsNotAvailable_shouldReturnFalse() {
        when(chatModelWrapper.isAvailable()).thenReturn(false);

        boolean result = aiAvailabilityService.execute();

        assertThat(result).isFalse();
    }
}
