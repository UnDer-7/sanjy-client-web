package br.com.gorillaroxo.sanjy.client.web.test.ai;

import java.util.List;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

/**
 * Fake ChatModel implementation for integration tests.
 * Returns a controlled JSON response that can be deserialized into DietPlanControllerRequestDto.
 * Compatible with GraalVM Native Image (no Mockito needed).
 */
public class FakeChatModel implements ChatModel {

    private static final String DEFAULT_RESPONSE = """
            {
                "name": "Test Diet Plan",
                "startDate": null,
                "endDate": null,
                "dailyCalories": 2000,
                "dailyProteinInG": 150,
                "dailyCarbsInG": 200,
                "dailyFatInG": 70,
                "goal": "Test goal from AI",
                "nutritionistNotes": "Test notes from AI",
                "mealTypes": [
                    {
                        "name": "Breakfast",
                        "scheduledTime": "08:00:00",
                        "observation": "Morning meal",
                        "standardOptions": [
                            {
                                "optionNumber": 1,
                                "description": "Eggs | Toast | Orange juice"
                            }
                        ]
                    },
                    {
                        "name": "Lunch",
                        "scheduledTime": "12:00:00",
                        "observation": "Afternoon meal",
                        "standardOptions": [
                            {
                                "optionNumber": 1,
                                "description": "Chicken | Rice | Salad"
                            }
                        ]
                    }
                ]
            }
            """;

    private String responseJson;

    public FakeChatModel() {
        this.responseJson = DEFAULT_RESPONSE;
    }

    public FakeChatModel(final String responseJson) {
        this.responseJson = responseJson;
    }

    /**
     * Updates the response that will be returned by the next call.
     */
    public void setResponse(final String responseJson) {
        this.responseJson = responseJson;
    }

    /**
     * Resets the response to the default value.
     */
    public void reset() {
        this.responseJson = DEFAULT_RESPONSE;
    }

    @Override
    public ChatResponse call(final Prompt prompt) {
        final AssistantMessage assistantMessage = new AssistantMessage(responseJson);
        final Generation generation = new Generation(assistantMessage, ChatGenerationMetadata.NULL);
        return new ChatResponse(List.of(generation));
    }
}
