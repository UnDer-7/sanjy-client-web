package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.MealRecordFeignClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.SearchMealRecordParamRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.presentation.SearchMealRecordDomain;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import br.com.gorillaroxo.sanjy.client.web.util.ThreadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchMealRecordService {

    private final MealRecordFeignClient mealRecordFeignClient;

    @Qualifier("applicationTaskExecutor")
    private final TaskExecutor taskExecutor;

    public SearchMealRecordDomain search(Integer pageNumber, Integer pageSize, Instant consumedAtAfter, Instant consumedAtBefore,
        Boolean isFreeMeal) {
        final CompletableFuture<PagedResponseDTO<MealRecordResponseDTO>> mealRecordFuture = ThreadUtils.supplyAsyncWithMDC(() -> {
            log.info(
                LogField.Placeholders.ONE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "Searching meal records asynchronously..."));

            return mealRecordFeignClient.searchMealRecords(
                SearchMealRecordParamRequestDTO.builder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .consumedAtAfter(consumedAtAfter)
                    .consumedAtBefore(consumedAtBefore)
                    .isFreeMeal(isFreeMeal)
                    .build());
        }, taskExecutor);

        final CompletableFuture<MealRecordStatisticsResponseDTO> mealRecordStatisticsFuture = ThreadUtils.supplyAsyncWithMDC(
            () -> {
                log.info(
                    LogField.Placeholders.ONE.placeholder,
                    StructuredArguments.kv(LogField.MSG.label(), "Searching meal records statistics asynchronously..."));

                return mealRecordFeignClient.getMealRecordStatisticsByDateRange(consumedAtAfter, consumedAtBefore);
            }, taskExecutor);

        return new SearchMealRecordDomain(
            mealRecordFuture.join(),
            mealRecordStatisticsFuture.join()
        );
    }

}
