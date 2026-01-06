package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.MealRecordFeignClient;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealRecordControllerRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordControllerResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.service.ActiveDietPlanService;
import br.com.gorillaroxo.sanjy.client.web.service.NewMealRecordService;
import br.com.gorillaroxo.sanjy.client.web.service.SearchMealRecordService;
import br.com.gorillaroxo.sanjy.client.web.service.TimezoneConversionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meal-record")
public class MealRecordController {

    private static final String ATTRIBUTE_MEAL_TYPES = "mealTypes";


    private final ActiveDietPlanService activeDietPlanService;
    private final SearchMealRecordService searchMealRecordService;
    private final TimezoneConversionService timezoneConversionService;
    private final NewMealRecordService newMealRecordService;

//    @GetMapping("/new")
//    public String showNewMealForm(Model model) {
//        // Adicionar objeto vazio para binding
//        model.addAttribute("mealRecordRequest", MealRecordRequestDTO.builder().build());
//
//        dietPlanActiveService.get()
//            .map(DietPlanResponseDTO::mealTypes)
//            .ifPresent(mealTypes -> model.addAttribute(ATTRIBUTE_MEAL_TYPES, mealTypes));
//
//        return LoggingHelper.loggingAndReturnControllerPagePath(TemplateConstants.PageNames.MEAL_NEW);
//    }

    @PostMapping
    public MealRecordControllerResponseDTO newMealRecord(@RequestBody @Valid @NotNull MealRecordControllerRequestDTO requestDTO) {
        return newMealRecordService.execute(requestDTO);
    }

//    @GetMapping("/today")
//    public String showTodayMeals(
//            @RequestParam(defaultValue = "0") Integer pageNumber,
//            @RequestParam(defaultValue = "20") Integer pageSize,
//            @RequestParam(required = false) @DateTimeFormat(pattern = RequestConstants.DateTimeFormats.DATE_TIME_LOCAL_FORMAT) LocalDateTime consumedAtAfter,
//            @RequestParam(required = false) @DateTimeFormat(pattern = RequestConstants.DateTimeFormats.DATE_TIME_LOCAL_FORMAT) LocalDateTime consumedAtBefore,
//            @RequestParam(required = false) Boolean isFreeMeal,
//            @RequestParam(required = false) String userTimezone,
//            @CookieValue(name = "sanjy-user-timezone", required = false) String userTimezoneCookie,
//            Model model) {
//
//        // Get timezone from parameter or cookie
//        String effectiveTimezone = getEffectiveTimezone(userTimezone, userTimezoneCookie);
//
//        // Initialize date filters to show today's meals by default (in user's timezone)
//        final LocalDateTime effectiveConsumedAtAfter = Objects.requireNonNullElse(consumedAtAfter, LocalDate.now().atTime(LocalTime.MIN));
//        final LocalDateTime effectiveConsumedAtBefore = Objects.requireNonNullElse(consumedAtBefore, LocalDate.now().atTime(LocalTime.MAX));
//
//        // Convert from user's timezone to UTC in the controller before calling service
//        final Instant utcConsumedAtAfter = timezoneConversionService.convertToUTC(effectiveConsumedAtAfter, effectiveTimezone);
//        final Instant utcConsumedAtBefore = timezoneConversionService.convertToUTC(effectiveConsumedAtBefore, effectiveTimezone);
//
//        final SearchMealRecordDomain searchResult = searchMealRecordService.search(
//            pageNumber,
//            pageSize,
//            utcConsumedAtAfter,
//            utcConsumedAtBefore,
//            isFreeMeal
//        );
//
//        final PagedResponseDTO<MealRecordResponseDTO> pagedMeals = searchResult.mealRecord();
//        final MealRecordStatisticsResponseDTO mealRecordStatistics = searchResult.mealRecordStatistics();
//
//        model.addAttribute("pagedMeals", pagedMeals);
//        model.addAttribute("totalPlannedMeals", mealRecordStatistics.plannedMealQuantity().intValue());
//        model.addAttribute("totalFreeMeals", mealRecordStatistics.freeMealQuantity().intValue());
//
//        // Filters to maintain state in the form
//        model.addAttribute("consumedAtAfter", effectiveConsumedAtAfter);
//        model.addAttribute("consumedAtBefore", effectiveConsumedAtBefore);
//        model.addAttribute("isFreeMeal", isFreeMeal);
//        model.addAttribute("userTimezone", effectiveTimezone);
//
//        return LoggingHelper.loggingAndReturnControllerPagePath(TemplateConstants.PageNames.MEAL_TODAY);
//    }

//    /**
//     * Get effective timezone from parameter or cookie.
//     * Priority: parameter > cookie
//     *
//     * @param paramTimezone timezone from request parameter
//     * @param cookieTimezone timezone from cookie
//     * @return the effective timezone to use
//     * @throws TimezoneNotProvidedException if both are null/empty
//     */
//    private String getEffectiveTimezone(String paramTimezone, String cookieTimezone) {
//        // Try parameter first
//        if (paramTimezone != null && !paramTimezone.isEmpty()) {
//            return paramTimezone;
//        }
//
//        // Fallback to cookie
//        if (cookieTimezone != null && !cookieTimezone.isEmpty()) {
//            return cookieTimezone;
//        }
//
//        // No timezone available - throw exception
//        throw new TimezoneNotProvidedException(
//            "Timezone is required. Please ensure JavaScript is enabled or visit the home page first to set your timezone."
//        );
//    }

}
