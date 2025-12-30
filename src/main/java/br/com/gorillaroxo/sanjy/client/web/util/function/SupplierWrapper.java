package br.com.gorillaroxo.sanjy.client.web.util.function;

import java.util.function.Supplier;

@FunctionalInterface
public interface SupplierWrapper<R, E extends Exception> {

    static <R> Supplier<R> wrap(final SupplierWrapper<R, Exception> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (final Exception e) {
                throw new ExceptionWrapper(e);
            }
        };
    }

    R get() throws E;
}
