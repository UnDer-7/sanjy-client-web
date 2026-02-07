package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import lombok.Builder;

@Builder
public record MealRecordStatisticsResponseDto(Long freeMealQuantity, Long plannedMealQuantity, Long mealQuantity) {}
