package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.client;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.DietPlanRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.handler.DietPlanNotFoundHandler;
import br.com.gorillaroxo.sanjy.client.web.exception.DietPlanNotFoundException;
import br.com.gorillaroxo.sanjy.client.web.exception.UnhandledClientHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class DietPlanRestClient {

    private static final String CLIENT_URL = "/v1/diet-plan";

    @Qualifier("sanjyServerRestClient")
    private final RestClient restClient;

    private final DietPlanNotFoundHandler dietPlanNotFoundHandler;

    /**
     * Creates a new diet plan with meal types (breakfast, lunch, snack, dinner, etc.), standard meal options,
     * nutritional targets, and goals. The new plan is automatically set as active and any previously active plan is
     * deactivated.
     *
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    public DietPlanResponseDto newDietPlan(final DietPlanRequestDto body) {
        return restClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(CLIENT_URL).build())
                .body(body)
                .retrieve()
                .body(DietPlanResponseDto.class);
    }

    /**
     * Retrieves the currently active diet plan with all meal types, standard options, nutritional targets (calories,
     * protein, carbs, fat), and goals. Only one diet plan can be active at a time.
     *
     * @throws DietPlanNotFoundException When diet plan is not found
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    public DietPlanResponseDto activeDietPlan() {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(CLIENT_URL).path("/active").build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, dietPlanNotFoundHandler::handler)
                .body(DietPlanResponseDto.class);
    }
}
