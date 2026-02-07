package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

public record MealRecordStatisticsControllerResponseDto(
        Long freeMealQuantity, Long plannedMealQuantity, Long mealQuantity) {}
