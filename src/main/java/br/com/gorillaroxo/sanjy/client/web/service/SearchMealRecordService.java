package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.MealRecordFeignClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.SearchMealRecordParamRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.SearchMealRecordParamControllerRequest;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.SearchMealRecordControllerResponseDTO;
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

    private final MealRecordFeignClient mealRecordFeignClient;
    private final MealRecordMapper mealRecordMapper;

    @Qualifier("applicationTaskExecutor")
    private final TaskExecutor taskExecutor;

    public SearchMealRecordControllerResponseDTO execute(final SearchMealRecordParamControllerRequest pageRequest) {

        final Instant consumedAtAfter = pageRequest.getConsumedAtAfter().toInstant();
        final Instant consumedAtBefore = pageRequest.getConsumedAtBefore().toInstant();

        final CompletableFuture<PagedResponseDTO<MealRecordResponseDTO>> mealRecordFuture =
                ThreadUtils.supplyAsyncWithMDC(
                        () -> {
                            log.info(
                                    LogField.Placeholders.TWO.placeholder,
                                    StructuredArguments.kv(
                                            LogField.MSG.label(), "Searching meal records asynchronously..."),
                                    StructuredArguments.kv(LogField.SEARCH_PARAMS.label(), "( " + pageRequest + " )"));

                            return mealRecordFeignClient.searchMealRecords(SearchMealRecordParamRequestDTO.builder()
                                    .pageNumber(pageRequest.getPageNumber())
                                    .pageSize(pageRequest.getPageSize())
                                    .consumedAtAfter(consumedAtAfter)
                                    .consumedAtBefore(consumedAtBefore)
                                    .isFreeMeal(pageRequest.getIsFreeMeal())
                                    .build());
                        },
                        taskExecutor);

        final CompletableFuture<MealRecordStatisticsResponseDTO> mealRecordStatisticsFuture =
                ThreadUtils.supplyAsyncWithMDC(
                        () -> {
                            log.info(
                                    LogField.Placeholders.ONE.placeholder,
                                    StructuredArguments.kv(
                                            LogField.MSG.label(),
                                            "Searching meal records statistics asynchronously..."));

                            return mealRecordFeignClient.getMealRecordStatisticsByDateRange(
                                    consumedAtAfter, consumedAtBefore);
                        },
                        taskExecutor);

        return new SearchMealRecordControllerResponseDTO(
                mealRecordMapper.toResponse(mealRecordFuture.join()),
                mealRecordMapper.toResponse(mealRecordStatisticsFuture.join()));
    }
}
