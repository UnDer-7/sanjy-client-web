package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.MealRecordFeignClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.MealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.config.TemplateConstants;
import br.com.gorillaroxo.sanjy.client.web.domain.SearchMealRecordDomain;
import br.com.gorillaroxo.sanjy.client.web.service.DietPlanActiveService;
import br.com.gorillaroxo.sanjy.client.web.service.SearchMealRecordService;
import br.com.gorillaroxo.sanjy.client.web.util.LoggingHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/meal")
public class MealRecordController {

    private static final String ATTRIBUTE_MEAL_TYPES = "mealTypes";

    private final MealRecordFeignClient mealRecordFeignClient;
    private final DietPlanActiveService dietPlanActiveService;
    private final SearchMealRecordService searchMealRecordService;

    @GetMapping("/new")
    public String showNewMealForm(Model model) {
        // Adicionar objeto vazio para binding
        model.addAttribute("mealRecordRequest", MealRecordRequestDTO.builder().build());

        dietPlanActiveService.get()
            .map(DietPlanResponseDTO::mealTypes)
            .ifPresent(mealTypes -> model.addAttribute(ATTRIBUTE_MEAL_TYPES, mealTypes));

        return LoggingHelper.loggingAndReturnControllerPagePath(TemplateConstants.PageNames.MEAL_NEW);
    }

    @PostMapping
    public String recordMeal(@ModelAttribute MealRecordRequestDTO request) {
        mealRecordFeignClient.newMealRecord(request);
        return LoggingHelper.loggingAndReturnControllerPagePath("redirect:/" + TemplateConstants.PageNames.MEAL_TODAY);
    }

    @GetMapping("/today")
    public String showTodayMeals(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime consumedAtAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime consumedAtBefore,
            @RequestParam(required = false) Boolean isFreeMeal,
            Model model) {

        // Initialize date filters to show today's meals by default
        final LocalDateTime effectiveConsumedAtAfter = Objects.requireNonNullElse(consumedAtAfter, LocalDate.now().atTime(LocalTime.MIN));

        final LocalDateTime effectiveConsumedAtBefore = Objects.requireNonNullElse(consumedAtBefore, LocalDate.now().atTime(LocalTime.MAX));

        final SearchMealRecordDomain searchResult = searchMealRecordService.search(
            pageNumber,
            pageSize,
            effectiveConsumedAtAfter.toInstant(ZoneOffset.UTC),
            effectiveConsumedAtBefore.toInstant(ZoneOffset.UTC),
            isFreeMeal
        );

        final PagedResponseDTO<MealRecordResponseDTO> pagedMeals = searchResult.mealRecord();
        final MealRecordStatisticsResponseDTO mealRecordStatistics = searchResult.mealRecordStatistics();

        model.addAttribute("pagedMeals", pagedMeals);
        model.addAttribute("totalPlannedMeals", mealRecordStatistics.plannedMealQuantity().intValue());
        model.addAttribute("totalFreeMeals", mealRecordStatistics.freeMealQuantity().intValue());

        // Filters to maintain state in the form
        model.addAttribute("consumedAtAfter", effectiveConsumedAtAfter);
        model.addAttribute("consumedAtBefore", effectiveConsumedAtBefore);
        model.addAttribute("isFreeMeal", isFreeMeal);

        return LoggingHelper.loggingAndReturnControllerPagePath(TemplateConstants.PageNames.MEAL_TODAY);
    }
}
