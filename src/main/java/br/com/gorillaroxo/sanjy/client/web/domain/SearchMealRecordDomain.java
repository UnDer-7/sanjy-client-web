package br.com.gorillaroxo.sanjy.client.web.domain;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDTO;

public record SearchMealRecordDomain(
    PagedResponseDTO<MealRecordResponseDTO> mealRecord,
    MealRecordStatisticsResponseDTO mealRecordStatistics
) {

}
