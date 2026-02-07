package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.Instant;
import lombok.Builder;

@Builder(toBuilder = true)
public record MetadataResponseDto(
        @JsonPropertyDescription("Timestamp when this resource was created in UTC timezone (ISO 8601 format). Example: "
                + RequestConstants.Examples.DATE_TIME)
        Instant createdAt,

        @JsonPropertyDescription(
                "Timestamp when this resource was last updated in UTC timezone (ISO 8601 format). Example: "
                        + RequestConstants.Examples.DATE_TIME)
        Instant updatedAt) {}
