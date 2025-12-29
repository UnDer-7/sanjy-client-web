package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MealRecordRequestDTO(
    Long mealTypeId,
    Boolean isFreeMeal,
    Long standardOptionId,
    String freeMealDescription,
    Double quantity,
    String unit,
    String notes,
    LocalDateTime consumedAt
) {

}
