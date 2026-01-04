package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.DietPlanFeignClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.DietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.service.DietPlanActiveService;
import br.com.gorillaroxo.sanjy.client.web.service.ProcessDietPlanFileService;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import br.com.gorillaroxo.sanjy.client.web.util.LoggingHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diet-plan")
public class DietPlanController {

    private static final String ATTRIBUTE_DIET_PLAN = "dietPlan";
    private static final String ATTRIBUTE_DIET_PLAN_REQUEST = "dietPlanRequest";

    private final DietPlanFeignClient dietPlanFeignClient;
    private final ProcessDietPlanFileService processDietPlanFileService;
    private final SanjyClientWebConfigProp sanjyClientWebConfigProp;
    private final DietPlanActiveService dietPlanActiveService;

    private static final Set<String> AVAILABLE_FILE_FILL_FORM_MEDIA_TYPES = Set.of(
        MediaType.APPLICATION_PDF_VALUE,
        MediaType.TEXT_MARKDOWN_VALUE,
        MediaType.TEXT_PLAIN_VALUE);

//    @GetMapping("/new")
//    public String showNewPlanForm(Model model) {
//        // Only add empty object if not already present (from flash attributes)
//        if (!model.containsAttribute(ATTRIBUTE_DIET_PLAN_REQUEST)) {
//            model.addAttribute(ATTRIBUTE_DIET_PLAN_REQUEST, DietPlanRequestDTO.builder().build());
//        }
//
//        // Add max file size for client-side validation
//        model.addAttribute("maxFileSizeInMb", sanjyClientWebConfigProp.upload().maxFileSizeInMb());
//
//        // Add allowed content types for client-side validation
//        model.addAttribute("allowedContentTypes", AVAILABLE_FILE_FILL_FORM_MEDIA_TYPES);
//
//        return LoggingHelper.loggingAndReturnControllerPagePath(TemplateConstants.PageNames.DIET_PLAN_NEW);
//    }

//    @PostMapping
//    public String createPlan(@ModelAttribute DietPlanRequestDTO request) {
//        dietPlanFeignClient.newDietPlan(request);
//
//        return LoggingHelper.loggingAndReturnControllerPagePath("redirect:/" + TemplateConstants.PageNames.DIET_PLAN_ACTIVE);
//    }

    @GetMapping
    public DietPlanResponseDTO showActivePlan() {
        return dietPlanActiveService.get().orElse(null);
    }

//    @PostMapping("/upload")
//    public String uploadAndFillForm(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
//        if (file.isEmpty()) {
//            log.warn(
//                LogField.Placeholders.ONE.placeholder,
//                StructuredArguments.kv(LogField.MSG.label(), "Empty file uploaded"));
//
//            model.addAttribute("error", "Please select a file to upload");
//            model.addAttribute(ATTRIBUTE_DIET_PLAN_REQUEST, DietPlanRequestDTO.builder().build());
//
//            return LoggingHelper.loggingAndReturnControllerPagePath(TemplateConstants.PageNames.DIET_PLAN_NEW);
//        }
//
//        if (!AVAILABLE_FILE_FILL_FORM_MEDIA_TYPES.contains(file.getContentType())) {
//            String validFormats = String.join(", ", AVAILABLE_FILE_FILL_FORM_MEDIA_TYPES);
//            String errorMessage = String.format("Invalid file format. Valid formats are: %s", validFormats);
//
//            log.warn(
//                LogField.Placeholders.THREE.placeholder,
//                StructuredArguments.kv(LogField.MSG.label(), "Invalid file format uploaded"),
//                StructuredArguments.kv(LogField.FILE_CONTENT_TYPE.label(), file.getContentType()),
//                StructuredArguments.kv(LogField.VALID_FILE_CONTENT_TYPES.label(), "( " + validFormats + " )"));
//
//            model.addAttribute("error", errorMessage);
//            model.addAttribute(ATTRIBUTE_DIET_PLAN_REQUEST, DietPlanRequestDTO.builder().build());
//
//            return LoggingHelper.loggingAndReturnControllerPagePath(TemplateConstants.PageNames.DIET_PLAN_NEW);
//        }
//
//        final Optional<DietPlanRequestDTO> dietPlan = processDietPlanFileService.process(file);
//
//        // Add to flash attributes to survive redirect
//        dietPlan.ifPresent(dp -> redirectAttributes.addFlashAttribute(ATTRIBUTE_DIET_PLAN_REQUEST, dp));
//
//        return dietPlan
//            .map(_ -> LoggingHelper.loggingAndReturnControllerPagePath("redirect:/" + TemplateConstants.PageNames.DIET_PLAN_NEW + "?uploaded=success"))
//            .orElseGet(() -> LoggingHelper.loggingAndReturnControllerPagePath("redirect:/" + TemplateConstants.PageNames.DIET_PLAN_NEW + "?uploaded=failure"));
//    }

}
