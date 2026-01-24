package br.com.gorillaroxo.sanjy.client.web.util.function;

import java.util.function.Consumer;

@FunctionalInterface
public interface ConsumerWrapper<T, E extends Exception> {

    static <T> Consumer<T> wrap(final ConsumerWrapper<T, Exception> func) {
        return arg -> {
            try {
                func.accept(arg);
            } catch (final Exception e) {
                throw new ExceptionWrapper(e);
            }
        };
    }

    void accept(T t) throws E;
}
