package br.com.gorillaroxo.sanjy.client.web;

import br.com.gorillaroxo.sanjy.client.web.client.github.dto.response.GitHubReleaseResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.DietPlanRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.MealRecordRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.PageRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.request.SearchMealRecordParamRequestDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.DietPlanResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordCreatedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.MetadataResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.PagedResponseDto;
import br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response.SanjyServerErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.config.TimezoneInitializer;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.DietPlanControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealRecordControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.MealTypeControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.PageRequestControllerDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.SearchMealRecordParamControllerRequest;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.request.StandardOptionControllerRequestDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.BooleanWrapperControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.DietPlanControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealRecordCreatedControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealTypeControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MealTypeSimplifiedControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.MetadataControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.SearchMealRecordControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.StandardOptionControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.StandardOptionSimplifiedControllerResponseDto;
import br.com.gorillaroxo.sanjy.client.web.validation.NoUnderscoreInHostnameValidator;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SanjyClientWebConfigProp.class)
@RegisterReflectionForBinding({
    BooleanWrapperControllerResponseDto.class,
    MealTypeControllerResponseDto.class,
    StandardOptionControllerRequestDto.class,
    MetadataControllerResponseDto.class,
    DietPlanControllerResponseDto.class,
    MetadataResponseDto.class,
    StandardOptionControllerResponseDto.class,
    MealTypeControllerRequestDto.class,
    MealTypeSimplifiedControllerResponseDto.class,
    StandardOptionSimplifiedControllerResponseDto.class,
    DietPlanControllerRequestDto.class,
    MealRecordControllerResponseDto.class,
    MealRecordControllerRequestDto.class,
    SearchMealRecordControllerResponseDto.class,
    SearchMealRecordParamControllerRequest.class,
    PageRequestControllerDto.class,
    ErrorResponseDto.class,
    SanjyClientWebConfigProp.class,
    NoUnderscoreInHostnameValidator.class,
    SanjyServerErrorResponseDto.class,
    DietPlanRequestDto.class,
    DietPlanResponseDto.class,
    MealRecordResponseDto.class,
    MealRecordRequestDto.class,
    PagedResponseDto.class,
    PageRequestDto.class,
    SearchMealRecordParamRequestDto.class,
    MealRecordStatisticsResponseDto.class,
    GitHubReleaseResponseDto.class,
    MealRecordCreatedControllerResponseDto.class,
    MealRecordCreatedResponseDto.class
})
public class SanjyClientWebApplication {

    private SanjyClientWebApplication() {}

    public static void main(String[] args) {
        final var app = new SpringApplication(SanjyClientWebApplication.class);
        app.addInitializers(new TimezoneInitializer());
        app.run(args);
    }
}
