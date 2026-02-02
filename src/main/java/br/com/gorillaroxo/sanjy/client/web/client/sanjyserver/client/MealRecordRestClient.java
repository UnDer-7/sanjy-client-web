package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.client;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.MealRecordRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.SearchMealRecordParamRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.exception.UnhandledClientHttpException;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MealRecordRestClient {

    @Qualifier("sanjyServerRestClient")
    private final RestClient restClient;

    private static final String CLIENT_URL = "/v1/meal-record";

    /**
     * Records a meal consumption with timestamp, meal type, and quantity. Can register either a standard meal
     * (following the diet plan by referencing a standard option) or a free meal (off-plan with custom description).
     *
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    public MealRecordResponseDto newMealRecord(final MealRecordRequestDto request) {
        return restClient.post()
            .uri(uriBuilder -> uriBuilder.path(CLIENT_URL).build())
            .body(request)
            .retrieve()
            .body(MealRecordResponseDto.class);
    }

    /**
     * Retrieves all meals consumed today, ordered by consumption time. Includes both standard meals (following the diet
     * plan) and free meals (off-plan). Use this to check daily food intake and diet adherence.
     *
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    public DietPlanResponseDto getTodayMealRecords(final ZoneId timezone) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder.path(CLIENT_URL).path("/today")
                .queryParam(RequestConstants.Query.TIMEZONE, timezone)
                .build())
            .retrieve()
            .body(DietPlanResponseDto.class);
    }

    /**
     * Searches meal records with pagination and optional filters (date range, meal type). Returns paginated results
     * with total count. Use this to view historical meal data, analyze eating patterns, or generate reports.
     *
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    public PagedResponseDto<MealRecordResponseDto> searchMealRecords(final SearchMealRecordParamRequestDto searchParams) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder.path(CLIENT_URL)
                .queryParamIfPresent(RequestConstants.Query.CONSUMED_AT_AFTER, Optional.ofNullable(searchParams.getConsumedAtAfter()))
                .queryParamIfPresent(RequestConstants.Query.CONSUMED_AT_BEFORE, Optional.ofNullable(searchParams.getConsumedAtBefore()))
                .queryParamIfPresent(RequestConstants.Query.IS_FREE_MEAL, Optional.ofNullable(searchParams.getIsFreeMeal()))
                .queryParamIfPresent(RequestConstants.Query.PAGE_NUMBER, Optional.ofNullable(searchParams.getPageNumber()))
                .queryParamIfPresent(RequestConstants.Query.PAGE_SIZE, Optional.ofNullable(searchParams.getPageSize()))
                .build())
            .retrieve()
            .body(new ParameterizedTypeReference<PagedResponseDto<MealRecordResponseDto>>() {});
    }

    /**
     * Retrieves aggregated statistics for meal records within a specified date range. Returns metrics such as total
     * meals consumed, breakdown by meal type (standard vs free meals), and nutritional totals. Use this to analyze
     * eating patterns, track diet adherence, and monitor nutritional intake over a period.
     *
     * @throws UnhandledClientHttpException When the request return an error (4xx or 5xx)
     */
    public MealRecordStatisticsResponseDto getMealRecordStatisticsByDateRange(final Instant consumedAtAfter, final Instant consumedAtBefore) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder.path("/statistics")
                .queryParamIfPresent(RequestConstants.Query.CONSUMED_AT_AFTER, Optional.ofNullable(consumedAtAfter))
                .queryParamIfPresent(RequestConstants.Query.CONSUMED_AT_BEFORE, Optional.ofNullable(consumedAtBefore))
                .build())
            .retrieve()
            .body(MealRecordStatisticsResponseDto.class);
    }
}
