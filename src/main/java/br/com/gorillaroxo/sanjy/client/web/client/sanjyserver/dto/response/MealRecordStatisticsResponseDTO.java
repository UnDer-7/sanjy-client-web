package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

public record MealRecordStatisticsResponseDTO(
    Long freeMealQuantity,
    Long plannedMealQuantity,
    Long mealQuantity
) {

}
