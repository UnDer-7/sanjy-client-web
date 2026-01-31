package br.com.gorillaroxo.sanjy.client.web.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogField {
    MSG,
    TRANSACTION_ID,
    HTTP_REQUEST,
    CORRELATION_ID,
    CHANNEL,
    EXCEPTION_CLASS,
    EXCEPTION_MESSAGE,
    ERROR_CODE,
    ERROR_TIMESTAMP,
    ERROR_MESSAGE,
    HTTP_STATUS_CODE,
    CUSTOM_EXCEPTION_STACK_TRACE,
    CUSTOM_ERROR_MESSAGE,
    EXCEPTION_CAUSE,
    EXCEPTION_CAUSE_MSG,
    PAGE_PATH,
    FEIGN_METHOD_KEY,
    REQUEST_METHOD,
    REQUEST_URL,
    REQUEST_HEADERS,
    REQUEST_BODY,
    RESPONSE_HEADERS,
    RESPONSE_BODY,
    FILE_CONTENT_TYPE,
    VALID_FILE_CONTENT_TYPES,
    INPUT_MESSAGE,
    DIET_PLAN_FILE_NAME,
    DIET_PLAN_FILE_CONTENT_TYPE,
    DIET_PLAN_FILE_SIZE_BYTES,
    DIET_PLAN_NAME,
    DIET_PLAN_GOAL,
    DIET_PLAN_NUTRITIONIST_NOTES,
    DIET_PLAN_MEAL_TYPE_SIZE,
    DIET_PLAN_MAX_FILE_SIZE_BYTES,
    FEIGN_RETRY_ENDPOINT,
    FEIGN_RETRY_COUNT,
    FEIGN_RETRY_INTERVAL,
    JSON_DESERIALIZATION_SOURCE,
    CLASS_DESERIALIZATION_TARGET,
    CLASS_NAME,
    CLASS_SERIALIZATION_SOURCE,
    DIET_PLAN_ID,
    DIET_PLAN_IS_ACTIVE,
    DIET_PLAN_CREATED_AT,
    PROJECT_CURRENT_VERSION,
    PROJECT_LATEST_VERSION,
    RUNTIME_MODE,
    APPLICATION_TIMEZONE,
    PROJECT_NAME,
    TIMEZONE_ID,
    SEARCH_PARAMS,
    AI_MODEL,
    AI_MAX_TOKENS,
    AI_TEMPERTURE,
    AI_STOP_SEQUENCES,
    AI_TOP_P,
    CONFIG_PROP;

    public String label() {
        return this.name().toLowerCase();
    }

    @Getter
    @RequiredArgsConstructor
    public enum Placeholders {
        ONE(createPlaceholder(1)),
        TWO(createPlaceholder(2)),
        THREE(createPlaceholder(3)),
        FOUR(createPlaceholder(4)),
        FIVE(createPlaceholder(5)),
        SIX(createPlaceholder(6)),
        SEVEN(createPlaceholder(7)),
        EIGHT(createPlaceholder(8)),
        NINE(createPlaceholder(9)),
        TEN(createPlaceholder(10));

        private final String placeholder;

        public static String createPlaceholder(final int total) {
            return "{} ".repeat(total);
        }
    }
}
