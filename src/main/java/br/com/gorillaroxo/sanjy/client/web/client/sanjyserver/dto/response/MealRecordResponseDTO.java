package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MealRecordResponseDTO(
        Long id,
        LocalDateTime consumedAt,
        MealTypeResponseDTO mealType,
        Boolean isFreeMeal,
        StandardOptionResponseDTO standardOption,
        String freeMealDescription,
        Double quantity,
        String unit,
        String notes,
        LocalDateTime createdAt
) {
}
