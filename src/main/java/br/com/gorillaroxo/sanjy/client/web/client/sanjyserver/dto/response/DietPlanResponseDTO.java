package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Builder(toBuilder = true)
public record DietPlanResponseDTO(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    Boolean isActive,
    Integer dailyCalories,
    Integer dailyProteinInG,
    Integer dailyCarbsInG,
    Integer dailyFatInG,
    String goal,
    String nutritionistNotes,
    Set<MealTypeResponseDTO> mealTypes,
    LocalDateTime createdAt
) {

    public DietPlanResponseDTO {
        mealTypes = Objects.requireNonNullElse(mealTypes, Collections.emptySet());
    }
}
