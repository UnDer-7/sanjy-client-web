package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.BooleanWrapperControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ProjectInfoMaintenanceControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.service.AiAvailabilityService;
import br.com.gorillaroxo.sanjy.client.web.service.MaintenanceProjectInfoService;
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
@RequestMapping("/api/v1/maintenance")
@Tag(name = "Maintenance", description = "")
public class MaintenanceController {

    private final AiAvailabilityService aiAvailabilityService;
    private final MaintenanceProjectInfoService maintenanceProjectInfoService;

    @GetMapping(value = "/ai/availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public BooleanWrapperControllerResponseDto isAvailable() {
        final boolean result = aiAvailabilityService.execute();
        return new BooleanWrapperControllerResponseDto(result);
    }

    @GetMapping(value = "/project-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectInfoMaintenanceControllerResponseDto projectInfo() {
        return maintenanceProjectInfoService.execute();
    }
}
