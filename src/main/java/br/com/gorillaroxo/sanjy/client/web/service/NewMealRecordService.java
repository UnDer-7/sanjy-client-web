package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.MealRecordFeignClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.MealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealRecordControllerRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordControllerResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.mapper.MealRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewMealRecordService {

    private final MealRecordFeignClient mealRecordFeignClient;
    private final MealRecordMapper mealRecordMapper;

    public MealRecordControllerResponseDTO execute(final MealRecordControllerRequestDTO requestDTO) {
        final MealRecordRequestDTO dto = mealRecordMapper.toDto(requestDTO);
        final MealRecordResponseDTO response = mealRecordFeignClient.newMealRecord(dto);
        return mealRecordMapper.toResponse(response);
    }
}
