package br.com.gorillaroxo.sanjy.client.web.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DietPlanConverterChatClientConfig {

    private final ChatClient.Builder ai;

    @Bean
    public ChatClient dietPlanConverterChatClient() {
        final var systemMsg = """
            Extract the information from the meal plan below and return it in the specified JSON format.
            
            JSON STRUCTURE:
            - A root object representing the complete DIET PLAN
            - Inside it, a "mealType" array with the MEAL TYPES
            - Each meal type contains a "standardOptions" array with the OPTIONS for that meal
            
            MANDATORY RULES:
            - DO NOT invent data. Use ONLY what is explicitly in the text
            - If a field is not in the text, leave it as null
            - Separate options by MEAL TYPE
            - Each meal type is a different object inside the "mealType" array
            - In the "description" field of each meal, separate the foods with " | " (pipe with spaces)
            """;

        return ai
            .defaultSystem(systemMsg)
            .defaultAdvisors(new SimpleLoggerAdvisor())
            .build();
    }

}
