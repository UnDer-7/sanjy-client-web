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
        public static final String TIMEZONE = "timezone";

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

    public static final class DateTimeFormats {
        public static final String DATE_FORMAT = "yyyy-MM-dd";

        public static final String TIME_FORMAT = "HH:mm:ss";
        public static final String DATE_TIME_FORMAT_UTC = "yyyy-MM-ddTHH:mm:ssZ";

        /**
         * Format for HTML datetime-local input (ISO 8601 without timezone)
         * Used for user input in their local timezone
         */
        public static final String DATE_TIME_LOCAL_FORMAT = "yyyy-MM-dd'T'HH:mm";

        private DateTimeFormats() {
            throw new IllegalStateException("Utility class");
        }
    }

    public static final class Examples {
        public static final String DATE_TIME = "2025-01-15T14:30:00Z";
        public static final String TIME = "14:30:00";
        public static final String DATE = "2025-01-15";
        public static final String TIMEZONE = "America/Sao_Paulo";

        private Examples() {
            throw new IllegalStateException("Utility class");
        }
    }
}
