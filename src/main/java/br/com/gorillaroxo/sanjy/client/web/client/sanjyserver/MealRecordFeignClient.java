package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.MealRecordRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.SearchMealRecordParamRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.interceptor.FeignInterceptor;
import br.com.gorillaroxo.sanjy.client.web.exception.UnhandledClientHttpException;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        value = "MealRecordFeignClient",
        url = "${sanjy-client-web.external-http-clients.sanjy-server.url}",
        path = "/v1/meal-record",
        configuration = FeignInterceptor.class)
public interface MealRecordFeignClient {

    /**
     * Records a meal consumption with timestamp, meal type, and quantity. Can register either a standard meal
     * (following the diet plan by referencing a standard option) or a free meal (off-plan with custom description).
     *
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    @PostMapping
    MealRecordResponseDto newMealRecord(@RequestBody MealRecordRequestDto mealRecordRequest);

    /**
     * Retrieves all meals consumed today, ordered by consumption time. Includes both standard meals (following the diet
     * plan) and free meals (off-plan). Use this to check daily food intake and diet adherence.
     *
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    @GetMapping("/today")
    List<MealRecordResponseDto> getTodayMealRecords(
            @RequestParam(required = false, name = RequestConstants.Query.TIMEZONE) ZoneId timezone);

    /**
     * Searches meal records with pagination and optional filters (date range, meal type). Returns paginated results
     * with total count. Use this to view historical meal data, analyze eating patterns, or generate reports.
     *
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    @GetMapping
    PagedResponseDto<MealRecordResponseDto> searchMealRecords(
            @SpringQueryMap SearchMealRecordParamRequestDto searchParams);

    /**
     * Retrieves aggregated statistics for meal records within a specified date range. Returns metrics such as total
     * meals consumed, breakdown by meal type (standard vs free meals), and nutritional totals. Use this to analyze
     * eating patterns, track diet adherence, and monitor nutritional intake over a period.
     *
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    @GetMapping("/statistics")
    MealRecordStatisticsResponseDto getMealRecordStatisticsByDateRange(
            @RequestParam(name = RequestConstants.Query.CONSUMED_AT_AFTER, required = false)
                    Instant consumedAtAfter,
            @RequestParam(name = RequestConstants.Query.CONSUMED_AT_BEFORE, required = false)
                    Instant consumedAtBefore);
}
