package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.DietPlanControllerRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.DietPlanControllerResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.exception.InvalidValuesException;
import br.com.gorillaroxo.sanjy.client.web.service.ActiveDietPlanService;
import br.com.gorillaroxo.sanjy.client.web.service.NewDietPlanService;
import br.com.gorillaroxo.sanjy.client.web.service.ProcessDietPlanFileService;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diet-plan")
@Tag(name = "Diet Plan", description = "Handles diet plan operations")
public class DietPlanController {

    private final ActiveDietPlanService activeDietPlanService;
    private final NewDietPlanService newDietPlanService;
    private final ProcessDietPlanFileService processDietPlanFileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DietPlanControllerResponseDTO create(@RequestBody @Valid @NonNull DietPlanControllerRequestDTO request) {
        return newDietPlanService.execute(request);
    }

    @GetMapping
    public DietPlanControllerResponseDTO get() {
        return activeDietPlanService.execute();
    }

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DietPlanControllerRequestDTO extractDietPlanFromFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn(
                    LogField.Placeholders.ONE.placeholder,
                    StructuredArguments.kv(LogField.MSG.label(), "Empty file uploaded"));
            throw new InvalidValuesException("Please select a file to upload");
        }

        return processDietPlanFileService.process(file);
    }
}
