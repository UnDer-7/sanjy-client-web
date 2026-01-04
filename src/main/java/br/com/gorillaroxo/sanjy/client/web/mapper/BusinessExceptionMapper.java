package br.com.gorillaroxo.sanjy.client.web.mapper;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.exception.BusinessException;
import br.com.gorillaroxo.sanjy.client.web.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = Constants.MAPSTRUCT_COMPONENT_MODEL,
    uses = OptionalMapper.class,
    unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BusinessExceptionMapper {

    @Mapping(target = "userCode", source = "exceptionCode.userCode")
    @Mapping(target = "userMessage", source = "exceptionCode.userMessage")
    ErrorResponseDto toDto(BusinessException exception);
}
