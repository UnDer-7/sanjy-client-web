package br.com.gorillaroxo.sanjy.client.web.mapper;

import br.com.gorillaroxo.sanjy.client.web.util.Constants;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = Constants.MAPSTRUCT_COMPONENT_MODEL, unmappedTargetPolicy = ReportingPolicy.ERROR)
public class DateTimeMapper {

    public Instant toInstant(final ZonedDateTime zonedDateTime) {
        return Optional.ofNullable(zonedDateTime)
                .map(ChronoZonedDateTime::toInstant)
                .orElse(null);
    }
}
