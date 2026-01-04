package br.com.gorillaroxo.sanjy.client.web.mapper;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.DietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.DietPlanControllerRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.DietPlanControllerResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = Constants.MAPSTRUCT_COMPONENT_MODEL,
    unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DietPlanMapper {

    DietPlanRequestDTO toDTO(DietPlanControllerRequestDTO requestDto);

    DietPlanControllerResponseDTO toController(DietPlanResponseDTO dto);
}
