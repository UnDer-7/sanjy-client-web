package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMealRecordParamRequestDto extends PageRequestDto {

    @JsonPropertyDescription("Filter meals consumed after this date/time, in UTC timezone (ISO 8601 format). Example: "
            + RequestConstants.Examples.DATE_TIME)
    private Instant consumedAtAfter;

    @JsonPropertyDescription("Filter meals consumed before this date/time, in UTC timezone (ISO 8601 format). Example: "
            + RequestConstants.Examples.DATE_TIME)
    private Instant consumedAtBefore;

    @JsonPropertyDescription("""
                Filter by meal type. True returns only free meals (off-plan), false returns only standard meals (following the diet plan). \
                If not specified, returns both types
                """)
    private Boolean isFreeMeal;
}
