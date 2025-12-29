package br.com.gorillaroxo.sanjy.client.web.config;

public final class TemplateConstants {

    private TemplateConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final class PageNames {
        private PageNames() {
            throw new IllegalStateException("Utility class");
        }

        public static final String DIET_PLAN_NEW = "diet-plan/new";
        public static final String DIET_PLAN_ACTIVE = "diet-plan/active";

        public static final String INDEX = "index";

        public static final String MEAL_NEW = "meal/new";
        public static final String MEAL_TODAY = "meal/today";
    }
}
