package br.com.gorillaroxo.sanjy.client.web.util.function;

import java.util.function.Predicate;

@FunctionalInterface
public interface PredicateWrapper<T, E extends Exception> {

    static <T> Predicate<T> wrap(final PredicateWrapper<T, Exception> func) {
        return arg -> {
            try {
                return func.test(arg);
            } catch (final Exception e) {
                throw new ExceptionWrapper(e);
            }
        };
    }

    boolean test(T arg) throws E;
}
