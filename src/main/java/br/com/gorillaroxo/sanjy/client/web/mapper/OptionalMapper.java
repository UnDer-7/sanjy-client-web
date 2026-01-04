package br.com.gorillaroxo.sanjy.client.web.mapper;

import br.com.gorillaroxo.sanjy.client.web.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;

@Mapper(
        componentModel = Constants.MAPSTRUCT_COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public class OptionalMapper {

    public <T> T fromOptional(final Optional<T> optional) {
        if (optional == null) {
            return null;
        }

        return optional.orElse(null);
    }

    public <T> Optional<T> toOptional(final T value) {
        return Optional.ofNullable(value);
    }
}
