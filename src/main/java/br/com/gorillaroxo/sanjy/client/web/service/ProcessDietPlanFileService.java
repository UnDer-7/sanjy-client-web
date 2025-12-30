package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.DietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.exception.DietPlanExtractorStrategyNotFoundException;
import br.com.gorillaroxo.sanjy.client.web.service.converter.DietPlanConverter;
import br.com.gorillaroxo.sanjy.client.web.service.extractor.ExtractTextFromFileStrategy;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessDietPlanFileService {

    private final Set<ExtractTextFromFileStrategy> extractors;
    private final DietPlanConverter dietPlanConverter;

    public Optional<DietPlanRequestDTO> process(final MultipartFile file) {
        log.info(
            LogField.Placeholders.FOUR.placeholder,
            StructuredArguments.kv(LogField.MSG.label(), "Request to process Diet Plan File"),
            StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
            StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
            StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize())
                );

        final String dietPlanTxt = extractors.stream()
            .filter(extractor -> extractor.accept(file))
            .findFirst()
            .orElseThrow(() -> new DietPlanExtractorStrategyNotFoundException("file content-type: %s".formatted(file.getContentType())))
            .extract(file);

        final Optional<DietPlanRequestDTO> converted = dietPlanConverter.convert(dietPlanTxt);

        converted.ifPresentOrElse(
            dietPlan -> log.info(
                LogField.Placeholders.EIGHT.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "Successfully finished processing Diet Plan file"),
                StructuredArguments.kv(LogField.DIET_PLAN_NAME.label(), dietPlan.name()),
                StructuredArguments.kv(LogField.DIET_PLAN_GOAL.label(), dietPlan.goal()),
                StructuredArguments.kv(LogField.DIET_PLAN_NUTRITIONIST_NOTES.label(), dietPlan.nutritionistNotes()),
                StructuredArguments.kv(LogField.DIET_PLAN_MEAL_TYPE_SIZE.label(), dietPlan.mealTypes().size()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize())),
            () -> log.warn(
                LogField.Placeholders.FOUR.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "Failed to process Diet Plan file, returning empty optional"),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize())));

        return converted;
    }
}
