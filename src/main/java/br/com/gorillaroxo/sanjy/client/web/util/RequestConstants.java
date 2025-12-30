package br.com.gorillaroxo.sanjy.client.web.util;

public final class RequestConstants {

    private RequestConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final class Query {
        public static final String CONSUMED_AT_AFTER = "consumedAtAfter";
        public static final String CONSUMED_AT_BEFORE = "consumedAtBefore";
        public static final String IS_FREE_MEAL = "isFreeMeal";
        public static final String PAGE_SIZE = "pageSize";
        public static final String PAGE_NUMBER = "pageNumber";

        private Query() {
            throw new IllegalStateException("Utility class");
        }
    }

    public static final class Headers {

        public static final String X_CORRELATION_ID = "X-Correlation-ID";
        public static final String X_CHANNEL = "X-Channel";

        private Headers() {
            throw new IllegalStateException("Utility class");
        }

    }

}
