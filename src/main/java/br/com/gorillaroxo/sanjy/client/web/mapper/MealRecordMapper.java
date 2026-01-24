package br.com.gorillaroxo.sanjy.client.web.mapper;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.MealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealRecordControllerRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordControllerResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordStatisticsControllerResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.PagedControllerResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = Constants.MAPSTRUCT_COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = DateTimeMapper.class)
public interface MealRecordMapper {

    MealRecordRequestDTO toDto(MealRecordControllerRequestDTO requestDTO);

    MealRecordControllerResponseDTO toResponse(MealRecordResponseDTO responseDTO);

    PagedControllerResponseDTO<MealRecordControllerResponseDTO> toResponse(
            PagedResponseDTO<MealRecordResponseDTO> responseDTO);

    MealRecordStatisticsControllerResponseDTO toResponse(MealRecordStatisticsResponseDTO responseDTO);
}
