package br.com.gorillaroxo.sanjy.client.web.config;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return JacksonConfig::dateTimeFormat;
    }

    private static void dateTimeFormat(final Jackson2ObjectMapperBuilder builder) {
        final DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.DATE_FORMAT);
        builder.serializers(new LocalDateSerializer(dateFormatter));
        builder.deserializers(new LocalDateDeserializer(dateFormatter));

        final DateTimeFormatter timeFormatter =
                DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.TIME_FORMAT);
        builder.serializers(new LocalTimeSerializer(timeFormatter));
        builder.deserializers(new LocalTimeDeserializer(timeFormatter));

        // Register custom ZonedDateTime deserializer that requires ZoneId in brackets
        builder.deserializerByType(ZonedDateTime.class, new StrictZonedDateTimeDeserializer());
    }

    /**
     * Custom deserializer for ZonedDateTime that enforces the presence of a ZoneId.
     * <p>
     * This deserializer rejects datetime strings that only contain an offset (e.g., "2026-01-05T20:54:30-02:00")
     * and requires the full ZoneId in brackets (e.g., "2026-01-05T20:54:30-02:00[America/Sao_Paulo]").
     * <p>
     * This ensures that the timezone information is complete and can handle DST (Daylight Saving Time) transitions correctly.
     */
    @Slf4j
    public static final class StrictZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

        @Override
        public ZonedDateTime deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {

            String text = parser.getText();

            if (text == null || text.isBlank()) {
                return null;
            }

            try {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(text, DateTimeFormatter.ISO_ZONED_DATE_TIME);

                if (zonedDateTime.getZone() instanceof ZoneOffset) {
                    final var errMsg = """
                        Invalid datetime format: ZoneId is required in brackets. \
                        Received: '%s'. Expected format: '%s' \
                        (e.g., %s)
                        """.formatted(text, RequestConstants.DateTimeFormats.DATE_TIME_FORMAT_TIMEZONE, RequestConstants.Examples.DATE_TIME_TIMEZONE);

                    log.warn(errMsg);
                    throw new IllegalArgumentException(errMsg);
                }

                return zonedDateTime;

            } catch (final DateTimeParseException e) {
                final var errMsg = """
                    Failed to parse datetime: '%s'. Expected format: '%s' \
                    (e.g., '%s')
                    """.formatted(text, RequestConstants.DateTimeFormats.DATE_TIME_FORMAT_TIMEZONE, RequestConstants.Examples.DATE_TIME_TIMEZONE);
                log.error(errMsg, e);
                throw new IllegalArgumentException(errMsg, e);
            }
        }
    }
}
