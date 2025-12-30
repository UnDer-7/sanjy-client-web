package br.com.gorillaroxo.sanjy.client.web.util.function;

import java.util.function.Function;

@FunctionalInterface
public interface FunctionWrapper<T, R, E extends Exception> {

    static <T, R>Function<T, R> wrap(final FunctionWrapper<T, R, Exception> function) {
        return arg -> {
            try {
                return function.accept(arg);
            } catch (final Exception e) {
                throw new ExceptionWrapper(e);
            }
        };
    }

    R accept(T t) throws E;
}
