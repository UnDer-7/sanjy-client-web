package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

public record SearchMealRecordControllerResponseDTO(
        PagedControllerResponseDTO<MealRecordControllerResponseDTO> page,
        MealRecordStatisticsControllerResponseDTO mealRecordStatistics) {}
