package br.com.gorillaroxo.sanjy.client.web.mapper;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.DietPlanRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.DietPlanControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.DietPlanControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = Constants.MAPSTRUCT_COMPONENT_MODEL, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DietPlanMapper {

    DietPlanRequestDto toDto(DietPlanControllerRequestDto requestDto);

    DietPlanControllerResponseDto toController(DietPlanResponseDto dto);
}
