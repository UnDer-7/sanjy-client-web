package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.DietPlanFeignClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.DietPlanControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.DietPlanControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.mapper.DietPlanMapper;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewDietPlanService {

    private final DietPlanFeignClient dietPlanFeignClient;
    private final DietPlanMapper dietPlanMapper;

    public DietPlanControllerResponseDto execute(final DietPlanControllerRequestDto requestBody) {
        log.info(
                LogField.Placeholders.FOUR.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request to create diet plan"),
                StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), requestBody.name()),
                StructuredArguments.kv(LogField.DIET_PLAN_GOAL.label(), requestBody.goal()),
                StructuredArguments.kv(
                        LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                        requestBody.mealTypes().size()));

        final var dto = dietPlanMapper.toDto(requestBody);
        final DietPlanResponseDto response = dietPlanFeignClient.newDietPlan(dto);

        log.info(
                LogField.Placeholders.SEVEN.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Successfully created diet plan"),
                StructuredArguments.kv(LogField.DIET_PLAN_ID.label(), response.id()),
                StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), response.name()),
                StructuredArguments.kv(LogField.DIET_PLAN_IS_ACTIVE.label(), response.isActive()),
                StructuredArguments.kv(LogField.DIET_PLAN_GOAL.label(), response.goal()),
                StructuredArguments.kv(
                        LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                        response.mealTypes().size()),
                StructuredArguments.kv(
                        LogField.DIET_PLAN_CREATED_AT.label(),
                        response.metadata().createdAt()));

        return dietPlanMapper.toController(response);
    }
}
