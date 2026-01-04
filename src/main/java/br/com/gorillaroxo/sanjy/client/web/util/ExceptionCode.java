package br.com.gorillaroxo.sanjy.client.web.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    UNEXPECTED_ERROR("001", "An unexpected internal error occurred."),
    UNHANDLED_CLIENT_HTTP("002", "A service integration error has occurred."),
    DIET_PLAN_EXTRACTOR_STRATEGY_NOT_FOUND("003", "Unable to process this file format. Please try a different file."),
    FAIL_TO_EXTRACT_TEXT_FROM_PDF_FILE("004", "There was a problem reading your PDF file. Please check the file and try again."),
    FAIL_TO_EXTRACT_TEXT_FROM_PLAIN_TEXT_FILE("005", "There was a problem reading your Plain Text file. Please check the file and try again."),
    FILE_MAX_UPLOAD_SIZE("006", "File size exceeds the maximum allowed limit. Please upload a smaller file."),
    SERIALIZATION_FAIL("007", "A data processing error has occurred. Please try again later."),
    DESERIALIZATION_FAIL("008", "Failed to process server response. Please try again later."),
    DIET_PLAN_NOT_FOUND("009", "Diet plan not found. Please create a new diet plan to get started."),
    TIMEZONE_NOT_PROVIDED("010", "Timezone is required but was not provided."),
    TIMEZONE_INVALID("011", "The provided timezone is not supported or invalid."),
    INVALID_VALUES("012", "Some informed value(s) is invalid");

    /**
     * Error code shown to the user
     */
    private final String userCode;

    /**
     * Error message shown to the user
     */
    private final String userMessage;
}
