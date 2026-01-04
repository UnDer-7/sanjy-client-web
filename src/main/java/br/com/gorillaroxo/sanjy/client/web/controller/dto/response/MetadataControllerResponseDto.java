package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record MetadataControllerResponseDto(
    @Schema(
        description = "Timestamp when this resource was created in UTC timezone (ISO 8601 format)",
        example = RequestConstants.Examples.DATE_TIME,
        format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT_UTC,
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonPropertyDescription("Timestamp when this resource was created in UTC timezone (ISO 8601 format). Example: " + RequestConstants.Examples.DATE_TIME)
    Instant createdAt,

    @Schema(
        description = "Timestamp when this resource was last updated in UTC timezone (ISO 8601 format)",
        example = RequestConstants.Examples.DATE_TIME,
        format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT_UTC,
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonPropertyDescription("Timestamp when this resource was last updated in UTC timezone (ISO 8601 format). Example: " + RequestConstants.Examples.DATE_TIME)
    Instant updatedAt
) {

}
