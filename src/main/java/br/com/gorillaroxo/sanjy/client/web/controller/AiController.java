package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.BooleanWrapperControllerResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.service.AiAvailabilityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai")
@Tag(name = "Artificial Intelligence", description = "AI related endpoints")
public class AiController {

    private final AiAvailabilityService aiAvailabilityService;

    @GetMapping(value = "/availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public BooleanWrapperControllerResponseDTO isAvailable() {
        final boolean result = aiAvailabilityService.execute();
        return new BooleanWrapperControllerResponseDTO(result);
    }
}
