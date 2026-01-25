package br.com.gorillaroxo.sanjy.client.web.client.config;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import feign.RetryableException;
import feign.Retryer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;

@Slf4j
@RequiredArgsConstructor
public class FeignRetryer implements Retryer {

    private int attempt = 1;
    private final SanjyClientWebConfigProp.HttpRetryProp httpRetryConfigProp;

    @Override
    public void continueOrPropagate(final RetryableException retryableException) {
        log.info(
                LogField.Placeholders.THREE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Feign HTTP client retry attempt"),
                StructuredArguments.kv(LogField.FEIGN_RETRY_ENDPOINT.label(), retryableException.getMessage()),
                StructuredArguments.kv(LogField.FEIGN_RETRY_COUNT.label(), attempt));

        if (attempt == httpRetryConfigProp.maxAttempt()) {
            throw retryableException;
        }

        attempt++;

        try {
            final long interval = calculateRetryInterval();
            log.info(
                    LogField.Placeholders.TWO.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Feign HTTP client waiting next retry attempt"),
                    StructuredArguments.kv(LogField.FEIGN_RETRY_INTERVAL.label(), interval));
            Thread.sleep(interval);
        } catch (final InterruptedException interruptedException) {
            log.warn(
                    LogField.Placeholders.ONE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Fail to wait interval"),
                    interruptedException);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    @SuppressWarnings({"checkstyle:NoClone", "checkstyle:SuperClone"})
    public Retryer clone() {
        return new FeignRetryer(httpRetryConfigProp);
    }

    private long calculateRetryInterval() {
        return (long) (httpRetryConfigProp.interval() * Math.pow(httpRetryConfigProp.backoffMultiplier(), attempt));
    }
}
