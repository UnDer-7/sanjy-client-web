package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.MealRecordFeignClient;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.MealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDTO;
import br.com.gorillaroxo.sanjy.client.web.config.TemplateConstants;
import br.com.gorillaroxo.sanjy.client.web.domain.SearchMealRecordDomain;
import br.com.gorillaroxo.sanjy.client.web.exception.TimezoneInvalidException;
import br.com.gorillaroxo.sanjy.client.web.exception.TimezoneNotProvidedException;
import br.com.gorillaroxo.sanjy.client.web.service.DietPlanActiveService;
import br.com.gorillaroxo.sanjy.client.web.service.SearchMealRecordService;
import br.com.gorillaroxo.sanjy.client.web.service.TimezoneConversionService;
import br.com.gorillaroxo.sanjy.client.web.util.LoggingHelper;
import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/meal")
public class MealRecordController {

    private static final String ATTRIBUTE_MEAL_TYPES = "mealTypes";

    private final MealRecordFeignClient mealRecordFeignClient;
    private final DietPlanActiveService dietPlanActiveService;
    private final SearchMealRecordService searchMealRecordService;
    private final TimezoneConversionService timezoneConversionService;

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
    public String recordMeal(
            @RequestParam(required = false) @DateTimeFormat(pattern = RequestConstants.DateTimeFormats.DATE_TIME_LOCAL_FORMAT) LocalDateTime consumedAt,
            @RequestParam(required = false) String userTimezone,
            @CookieValue(name = "sanjy-user-timezone", required = false) String userTimezoneCookie,
            @RequestParam Long mealTypeId,
            @RequestParam Boolean isFreeMeal,
            @RequestParam(required = false) Long standardOptionId,
            @RequestParam(required = false) String freeMealDescription,
            @RequestParam(required = false) Double quantity,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String notes) {

        // Get timezone from parameter or cookie
        String effectiveTimezone = getEffectiveTimezone(userTimezone, userTimezoneCookie);

        // Convert consumedAt from user's timezone to UTC in the controller
        Instant utcConsumedAt = Optional.ofNullable(consumedAt)
            .map(c -> timezoneConversionService.convertToUTC(c, effectiveTimezone))
            .orElse(null);

        // Build the request DTO with the converted UTC time
        MealRecordRequestDTO request = MealRecordRequestDTO.builder()
            .mealTypeId(mealTypeId)
            .consumedAt(utcConsumedAt)
            .isFreeMeal(isFreeMeal)
            .standardOptionId(standardOptionId)
            .freeMealDescription(freeMealDescription)
            .quantity(quantity)
            .unit(unit)
            .notes(notes)
            .build();

        mealRecordFeignClient.newMealRecord(request);
        return LoggingHelper.loggingAndReturnControllerPagePath("redirect:/" + TemplateConstants.PageNames.MEAL_TODAY);
    }

    @GetMapping("/today")
    public String showTodayMeals(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(pattern = RequestConstants.DateTimeFormats.DATE_TIME_LOCAL_FORMAT) LocalDateTime consumedAtAfter,
            @RequestParam(required = false) @DateTimeFormat(pattern = RequestConstants.DateTimeFormats.DATE_TIME_LOCAL_FORMAT) LocalDateTime consumedAtBefore,
            @RequestParam(required = false) Boolean isFreeMeal,
            @RequestParam(required = false) String userTimezone,
            @CookieValue(name = "sanjy-user-timezone", required = false) String userTimezoneCookie,
            Model model) {

        // Get timezone from parameter or cookie
        String effectiveTimezone = getEffectiveTimezone(userTimezone, userTimezoneCookie);

        // Initialize date filters to show today's meals by default (in user's timezone)
        final LocalDateTime effectiveConsumedAtAfter = Objects.requireNonNullElse(consumedAtAfter, LocalDate.now().atTime(LocalTime.MIN));
        final LocalDateTime effectiveConsumedAtBefore = Objects.requireNonNullElse(consumedAtBefore, LocalDate.now().atTime(LocalTime.MAX));

        // Convert from user's timezone to UTC in the controller before calling service
        final Instant utcConsumedAtAfter = timezoneConversionService.convertToUTC(effectiveConsumedAtAfter, effectiveTimezone);
        final Instant utcConsumedAtBefore = timezoneConversionService.convertToUTC(effectiveConsumedAtBefore, effectiveTimezone);

        final SearchMealRecordDomain searchResult = searchMealRecordService.search(
            pageNumber,
            pageSize,
            utcConsumedAtAfter,
            utcConsumedAtBefore,
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
        model.addAttribute("userTimezone", effectiveTimezone);

        return LoggingHelper.loggingAndReturnControllerPagePath(TemplateConstants.PageNames.MEAL_TODAY);
    }

    /**
     * Get effective timezone from parameter or cookie.
     * Priority: parameter > cookie
     *
     * @param paramTimezone timezone from request parameter
     * @param cookieTimezone timezone from cookie
     * @return the effective timezone to use
     * @throws TimezoneNotProvidedException if both are null/empty
     */
    private String getEffectiveTimezone(String paramTimezone, String cookieTimezone) {
        // Try parameter first
        if (paramTimezone != null && !paramTimezone.isEmpty()) {
            return paramTimezone;
        }

        // Fallback to cookie
        if (cookieTimezone != null && !cookieTimezone.isEmpty()) {
            return cookieTimezone;
        }

        // No timezone available - throw exception
        throw new TimezoneNotProvidedException(
            "Timezone is required. Please ensure JavaScript is enabled or visit the home page first to set your timezone."
        );
    }

}
