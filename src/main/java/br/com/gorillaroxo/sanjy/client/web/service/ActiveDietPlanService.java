package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.client.DietPlanRestClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
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
public class ActiveDietPlanService {

    private final DietPlanRestClient dietPlanRestClient;
    private final DietPlanMapper dietPlanMapper;

    public DietPlanControllerResponseDto execute() {
        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Request to get active diet plan"));

        final DietPlanResponseDto dietPlan = dietPlanRestClient.activeDietPlan();

        log.info(
                LogField.Placeholders.SEVEN.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Successfully got active diet plan"),
                StructuredArguments.kv(LogField.DIET_PLAN_ID.label(), dietPlan.id()),
                StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), dietPlan.name()),
                StructuredArguments.kv(LogField.DIET_PLAN_IS_ACTIVE.label(), dietPlan.isActive()),
                StructuredArguments.kv(LogField.DIET_PLAN_GOAL.label(), dietPlan.goal()),
                StructuredArguments.kv(
                        LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(),
                        dietPlan.mealTypes().size()),
                StructuredArguments.kv(
                        LogField.DIET_PLAN_CREATED_AT.label(),
                        dietPlan.metadata().createdAt()));

        return dietPlanMapper.toController(dietPlan);
    }
}
