package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.client.MealRecordRestClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.MealRecordRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealRecordControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.mapper.MealRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewMealRecordService {

    private final MealRecordRestClient mealRecordRestClient;
    private final MealRecordMapper mealRecordMapper;

    public MealRecordControllerResponseDto execute(final MealRecordControllerRequestDto mealRecordRequest) {
        final MealRecordRequestDto dto = mealRecordMapper.toDto(mealRecordRequest);
        final MealRecordResponseDto response = mealRecordRestClient.newMealRecord(dto);
        return mealRecordMapper.toResponse(response);
    }
}
