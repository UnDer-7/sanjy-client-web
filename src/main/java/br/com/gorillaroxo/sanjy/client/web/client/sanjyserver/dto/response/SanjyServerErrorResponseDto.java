package br.com.gorillaroxo.sanjy.client.web.client.sanjyserver.dto.response;

import lombok.Builder;

@Builder
public record SanjyServerErrorResponseDto(
        String code, String timestamp, String message, String customMessage, int httpStatusCode) {

    public static final String ERROR_CODE_DIET_PLAN_NOT_FOUND = "003";
}
