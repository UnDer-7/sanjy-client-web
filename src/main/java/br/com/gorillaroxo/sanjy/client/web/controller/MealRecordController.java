package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealRecordControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.SearchMealRecordParamControllerRequest;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.SearchMealRecordControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.service.NewMealRecordService;
import br.com.gorillaroxo.sanjy.client.web.service.SearchMealRecordService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meal-record")
public class MealRecordController {

    private final SearchMealRecordService searchMealRecordService;
    private final NewMealRecordService newMealRecordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MealRecordControllerResponseDto create(
            @RequestBody @Valid @NotNull MealRecordControllerRequestDto mealRecordRequest) {
        return newMealRecordService.execute(mealRecordRequest);
    }

    @GetMapping
    public SearchMealRecordControllerResponseDto search(
            @Parameter(hidden = true) @NotNull @Valid final SearchMealRecordParamControllerRequest pageRequest) {
        return searchMealRecordService.execute(pageRequest);
    }
}
