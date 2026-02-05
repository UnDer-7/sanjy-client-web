package br.com.gorillaroxo.sanjy.client.web.mapper;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.MealRecordRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordCreatedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealRecordControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordCreatedControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordStatisticsControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.PagedControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = Constants.MAPSTRUCT_COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = DateTimeMapper.class)
public interface MealRecordMapper {

    MealRecordRequestDto toDto(MealRecordControllerRequestDto request);

    MealRecordControllerResponseDto toResponse(MealRecordResponseDto response);
    MealRecordCreatedControllerResponseDto toResponse(MealRecordCreatedResponseDto response);

    PagedControllerResponseDto<MealRecordControllerResponseDto> toResponse(
            PagedResponseDto<MealRecordResponseDto> pagedResponse);

    MealRecordStatisticsControllerResponseDto toResponse(MealRecordStatisticsResponseDto statisticsResponse);
}
