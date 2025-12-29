package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.DietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.exception.DietPlanNotFoundException;
import br.com.gorillaroxo.sanjy.client.web.exception.UnhandledClientHttpException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    value = "br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.DietPlanFeignClient",
    url = "${sanjy-client.external-apis.sanjy-server.url}",
    path = "/v1/diet-plan"
)
public interface DietPlanFeignClient {

    /**
     * Retrieves the currently active diet plan with all meal types, standard options,
     * nutritional targets (calories, protein, carbs, fat), and goals. Only one diet plan can be active at a time.
     * @throws DietPlanNotFoundException When diet plan is not found
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    @GetMapping("/active")
    DietPlanResponseDTO activeDietPlan();

    /**
     * Creates a new diet plan with meal types (breakfast, lunch, snack, dinner, etc.), standard meal options, nutritional targets, and goals.
     * The new plan is automatically set as active and any previously active plan is deactivated.
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    @PostMapping
    DietPlanResponseDTO newDietPlan(@RequestBody DietPlanRequestDTO dietPlan);
}
