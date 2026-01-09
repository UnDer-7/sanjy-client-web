package br.com.gorillaroxo.sanjy.client.web.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class PageRequestControllerDTO {

    @NotNull
    @PositiveOrZero
    @JsonPropertyDescription("Page number to retrieve (zero-based, where 0 is the first page)")
    private Integer pageNumber;

    @Positive
    @Builder.Default
    @JsonPropertyDescription("Number of items per page. If not specified, returns 10 items per page")
    private Integer pageSize = 10;
}
