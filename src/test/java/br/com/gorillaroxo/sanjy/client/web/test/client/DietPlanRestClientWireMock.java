package br.com.gorillaroxo.sanjy.client.web.test.client;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Getter
@Component
@Accessors(fluent = true)
public class DietPlanRestClientWireMock {

    private final NewDietPlan newDietPlan = new NewDietPlan();

    public static class NewDietPlan {
        public void generic(final HttpStatus httpStatus, final String xCorrelationId, final String responseBody) {
            stubFor(post(urlPathEqualTo("/sanjy-server/v1/diet-plan"))
                .withHeader(RequestConstants.Headers.X_CORRELATION_ID, equalTo(xCorrelationId))
                .withHeader(RequestConstants.Headers.X_CHANNEL, not(absent()))
                .willReturn(aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withStatus(httpStatus.value())
                    .withBody(responseBody)));
        }
    }
}
