package br.com.gorillaroxo.sanjy.client.web.controller;

import br.com.gorillaroxo.sanjy.client.web.config.TemplateConstants;
import br.com.gorillaroxo.sanjy.client.web.util.LoggingHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home() {
        return LoggingHelper.loggingAndReturnControllerPagePath(TemplateConstants.PageNames.INDEX);
    }
}
