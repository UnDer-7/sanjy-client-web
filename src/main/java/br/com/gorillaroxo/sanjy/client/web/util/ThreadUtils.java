package br.com.gorillaroxo.sanjy.client.web.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import org.slf4j.MDC;

public final class ThreadUtils {

    private ThreadUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void runAsyncWithMdc(final Runnable runnable, final Executor executor) {
        Objects.requireNonNull(runnable);
        Objects.requireNonNull(executor);

        final Map<String, String> mdcCtx = Objects.requireNonNullElseGet(MDC.getCopyOfContextMap(), HashMap::new);

        CompletableFuture.runAsync(
                () -> {
                    MDC.setContextMap(mdcCtx);
                    try {
                        runnable.run();
                    } finally {
                        MDC.clear();
                    }
                },
                executor);
    }

    public static void runAsyncWithMdc(final Runnable runnable) {
        Objects.requireNonNull(runnable);

        final Map<String, String> mdcCtx = Objects.requireNonNullElseGet(MDC.getCopyOfContextMap(), HashMap::new);

        CompletableFuture.runAsync(() -> {
            MDC.setContextMap(mdcCtx);
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        });
    }

    public static <T> CompletableFuture<T> supplyAsyncWithMDC(final Supplier<T> supplier, final Executor executor) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(executor);

        final Map<String, String> mdcCtx = Objects.requireNonNullElseGet(MDC.getCopyOfContextMap(), HashMap::new);

        return CompletableFuture.supplyAsync(
                () -> {
                    MDC.setContextMap(mdcCtx);
                    try {
                        return supplier.get();
                    } finally {
                        MDC.clear();
                    }
                },
                executor);
    }

    public static <T> CompletableFuture<T> supplyAsyncWithMDC(final Supplier<T> supplier) {
        Objects.requireNonNull(supplier);

        final Map<String, String> mdcCtx = Objects.requireNonNullElseGet(MDC.getCopyOfContextMap(), HashMap::new);

        return CompletableFuture.supplyAsync(() -> {
            MDC.setContextMap(mdcCtx);
            try {
                return supplier.get();
            } finally {
                MDC.clear();
            }
        });
    }
}
