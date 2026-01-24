package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
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
public class PageRequestDTO {

    @JsonPropertyDescription("Page number to retrieve (zero-based, where 0 is the first page)")
    private Integer pageNumber;

    @JsonPropertyDescription("Number of items per page. If not specified, returns 10 items per page")
    private Integer pageSize;
}
