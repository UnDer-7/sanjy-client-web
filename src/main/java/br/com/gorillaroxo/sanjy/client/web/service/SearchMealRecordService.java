package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.client.MealRecordRestClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.SearchMealRecordParamRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.SearchMealRecordParamControllerRequest;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.SearchMealRecordControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.mapper.MealRecordMapper;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import br.com.gorillaroxo.sanjy.client.web.util.ThreadUtils;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchMealRecordService {

    private final MealRecordRestClient mealRecordRestClient;
    private final MealRecordMapper mealRecordMapper;

    @Qualifier("applicationTaskExecutor")
    private final TaskExecutor taskExecutor;

    public SearchMealRecordControllerResponseDto execute(final SearchMealRecordParamControllerRequest pageRequest) {

        final Instant consumedAtAfter = pageRequest.getConsumedAtAfter().toInstant();
        final Instant consumedAtBefore = pageRequest.getConsumedAtBefore().toInstant();

        final CompletableFuture<PagedResponseDto<MealRecordResponseDto>> mealRecordFuture =
                ThreadUtils.supplyAsyncWithMdc(
                        () -> {
                            log.info(
                                    LogField.Placeholders.TWO.getPlaceholder(),
                                    StructuredArguments.kv(
                                            LogField.MSG.label(), "Searching meal records asynchronously..."),
                                    StructuredArguments.kv(LogField.SEARCH_PARAMS.label(), "( " + pageRequest + " )"));

                            //noinspection S3252 - Lombok @SuperBuilder generates builder() in each class; accessing via derived type is correct here
                            return mealRecordRestClient.searchMealRecords(SearchMealRecordParamRequestDto.builder()
                                    .pageNumber(pageRequest.getPageNumber())
                                    .pageSize(pageRequest.getPageSize())
                                    .consumedAtAfter(consumedAtAfter)
                                    .consumedAtBefore(consumedAtBefore)
                                    .isFreeMeal(pageRequest.getIsFreeMeal())
                                    .build());
                        },
                        taskExecutor);

        final CompletableFuture<MealRecordStatisticsResponseDto> mealRecordStatisticsFuture =
                ThreadUtils.supplyAsyncWithMdc(
                        () -> {
                            log.info(
                                    LogField.Placeholders.ONE.getPlaceholder(),
                                    StructuredArguments.kv(
                                            LogField.MSG.label(),
                                            "Searching meal records statistics asynchronously..."));

                            return mealRecordRestClient.getMealRecordStatisticsByDateRange(
                                    consumedAtAfter, consumedAtBefore);
                        },
                        taskExecutor);

        return new SearchMealRecordControllerResponseDto(
                mealRecordMapper.toResponse(mealRecordFuture.join()),
                mealRecordMapper.toResponse(mealRecordStatisticsFuture.join()));
    }
}
