package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import lombok.Builder;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Builder
public record MealTypeResponseDTO(
    Long id,
    String name,
    LocalTime scheduledTime,
    String observation,
    Long dietPlanId,
    List<StandardOptionResponseDTO> standardOptions
) {

    public MealTypeResponseDTO {
        standardOptions = Objects.requireNonNullElse(standardOptions, Collections.emptyList());
    }
}
