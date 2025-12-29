package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMealRecordParamRequestDTO extends PageRequestDTO {

    private LocalDateTime consumedAtAfter;
    private LocalDateTime consumedAtBefore;
    private Boolean isFreeMeal;

}
