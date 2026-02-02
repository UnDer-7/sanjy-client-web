package br.com.gorillaroxo.sanjy.client.web.client.config;

import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public final class BodyUtils {

    private BodyUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String readBody(final byte[] body, final MediaType mediaType, final String errMsg) {
        try {
            if (body == null || body.length == 0) {
                return "<empty>";
            }
            final String type = mediaType.toString();

            if (type.contains("json") || type.contains("xml") || type.contains("text") || type.contains("html") || type.contains("yml") || type.contains("yaml")) {
                return new String(body, StandardCharsets.UTF_8);
            }

            return "<binary, " + body.length + " bytes>";
        } catch (final Exception e) {
            log.warn(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), errMsg),
                e);
            return "<" + errMsg + ">";
        }
    }

    public static String readBody(final ClientHttpResponse response) {
        try (InputStream is = response.getBody()) {
            return readBody(is.readAllBytes(), response.getHeaders().getContentType(), "Could not read response body");
        } catch (final Exception e) {
            log.warn(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Could not read response body"),
                e);
            return "<Could not read response body>";
        }
    }
}
