package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request;

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
    private Integer pageNumber;
    private Integer pageSize;
}