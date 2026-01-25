package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.DietPlanRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.interceptor.FeignInterceptor;
import br.com.gorillaroxo.sanjy.client.web.exception.DietPlanNotFoundException;
import br.com.gorillaroxo.sanjy.client.web.exception.UnhandledClientHttpException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        value = "br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.DietPlanFeignClient",
        url = "${sanjy-client-web.external-http-clients.sanjy-server.url}",
        path = "/v1/diet-plan",
        configuration = FeignInterceptor.class)
public interface DietPlanFeignClient {

    /**
     * Creates a new diet plan with meal types (breakfast, lunch, snack, dinner, etc.), standard meal options,
     * nutritional targets, and goals. The new plan is automatically set as active and any previously active plan is
     * deactivated.
     *
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    @PostMapping
    DietPlanResponseDto newDietPlan(@RequestBody DietPlanRequestDto dietPlan);

    /**
     * Retrieves the currently active diet plan with all meal types, standard options, nutritional targets (calories,
     * protein, carbs, fat), and goals. Only one diet plan can be active at a time.
     *
     * @throws DietPlanNotFoundException When diet plan is not found
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    @GetMapping("/active")
    DietPlanResponseDto activeDietPlan();
}
