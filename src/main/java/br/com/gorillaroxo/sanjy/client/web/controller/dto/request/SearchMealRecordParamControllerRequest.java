package br.com.gorillaroxo.sanjy.client.web.controller.dto.request;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class SearchMealRecordParamControllerRequest extends PageRequestControllerDTO {

    @NotNull
    @JsonPropertyDescription("Filter meals consumed after this date/time, in UTC timezone (ISO 8601 format). Example: " + RequestConstants.Examples.DATE_TIME)
    private ZonedDateTime consumedAtAfter;

    @NotNull
    @JsonPropertyDescription("Filter meals consumed before this date/time, in UTC timezone (ISO 8601 format). Example: " + RequestConstants.Examples.DATE_TIME)
    private ZonedDateTime consumedAtBefore;

    @JsonPropertyDescription("""
                Filter by meal type. True returns only free meals (off-plan), false returns only standard meals (following the diet plan). \
                If not specified, returns both types
                """)
    private Boolean isFreeMeal;

}
