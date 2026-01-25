package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

public record SearchMealRecordControllerResponseDto(
        PagedControllerResponseDto<MealRecordControllerResponseDto> page,
        MealRecordStatisticsControllerResponseDto mealRecordStatistics) {}
