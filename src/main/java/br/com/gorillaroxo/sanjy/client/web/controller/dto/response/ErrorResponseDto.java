package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = """
        Standard error response DTO returned when any exception occurs in the API. \
        All fields except customMessage are always present. \
        This response follows a consistent error handling pattern across all endpoints.
        """)
public record ErrorResponseDto(
        @Schema(
                description = "Unique error code identifying the type of error that occurred",
                example = "003",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String userCode,

        @Schema(
                description = "ISO 8601 zoned time indicating exactly when the error occurred on the server",
                example = RequestConstants.Examples.DATE_TIME,
                format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT_UTC,
                requiredMode = Schema.RequiredMode.REQUIRED)
        Instant timestamp,

        @Schema(
                description = "Standard human-readable error message describing the error",
                example = "Diet plan was not found",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String userMessage,

        @Schema(
                description = """
                Optional additional context or details about this specific error occurrence. \
                This field is only present when the error has specific contextual information to provide. \
                Due to @JsonInclude(NON_EMPTY), this field is omitted from the response when null or empty.
                """,
                example = "No active diet plan found for user ID 456",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String customMessage,

        @Schema(description = """
                    HTTP status code of the error response (400 for validation errors, 404 for not found, 500 for server errors, etc.)
                    """, example = "404", requiredMode = Schema.RequiredMode.REQUIRED)
        int httpStatusCode) {}
