package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.config.TemplateConstants;
import br.com.gorillaroxo.sanjy.client.web.util.LoggingHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingsController {

    @GetMapping
    public String showSettings(Model model) {
        // Get all available timezones
        List<String> timezones = ZoneId.getAvailableZoneIds()
            .stream()
            .sorted()
            .collect(Collectors.toList());

        model.addAttribute("timezones", timezones);

        return LoggingHelper.loggingAndReturnControllerPagePath(TemplateConstants.PageNames.SETTINGS);
    }
}
