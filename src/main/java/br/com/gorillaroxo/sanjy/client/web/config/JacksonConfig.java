package br.com.gorillaroxo.sanjy.client.web.config;

import br.com.gorillaroxo.sanjy.client.web.util.RequestConstants;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer jsonCustomizer() {
        return JacksonConfig::dateTimeFormat;
    }

    private static void dateTimeFormat(final JsonMapper.Builder builder) {
        final DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.DATE_FORMAT);
        final DateTimeFormatter timeFormatter =
                DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.TIME_FORMAT);

        final SimpleModule module = new SimpleModule("SanjyDateTimeModule");
        module.addSerializer(new LocalDateSerializer(dateFormatter));
        module.addDeserializer(java.time.LocalDate.class, new LocalDateDeserializer(dateFormatter));
        module.addSerializer(new LocalTimeSerializer(timeFormatter));
        module.addDeserializer(java.time.LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        module.addSerializer(ZonedDateTime.class, new ZonedDateTimeWithZoneIdSerializer());
        module.addDeserializer(ZonedDateTime.class, new StrictZonedDateTimeDeserializer());

        builder.addModule(module);
    }

    /**
     * Custom serializer for ZonedDateTime that includes the ZoneId in brackets.
     *
     * <p>This serializer outputs datetime strings with the full ZoneId (e.g.,
     * "2026-01-05T20:54:30-02:00[America/Sao_Paulo]") to match the expected format for deserialization.
     */
    @Slf4j
    public static final class ZonedDateTimeWithZoneIdSerializer extends ValueSerializer<ZonedDateTime> {

        @Override
        public void serialize(
                final ZonedDateTime value, final JsonGenerator gen, final SerializationContext serializers)
                throws JacksonException {

            if (value == null) {
                gen.writeNull();
                return;
            }

            // Format using ISO_ZONED_DATE_TIME which includes the zone ID in brackets
            gen.writeString(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        }
    }

    /**
     * Custom deserializer for ZonedDateTime that enforces the presence of a ZoneId.
     *
     * <p>This deserializer rejects datetime strings that only contain an offset (e.g., "2026-01-05T20:54:30-02:00") and
     * requires the full ZoneId in brackets (e.g., "2026-01-05T20:54:30-02:00[America/Sao_Paulo]").
     *
     * <p>This ensures that the timezone information is complete and can handle DST (Daylight Saving Time) transitions
     * correctly.
     */
    @Slf4j
    public static final class StrictZonedDateTimeDeserializer extends ValueDeserializer<ZonedDateTime> {

        @Override
        public ZonedDateTime deserialize(final JsonParser parser, final DeserializationContext context)
                throws JacksonException {

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
                        """.formatted(
                                    text,
                                    RequestConstants.DateTimeFormats.DATE_TIME_FORMAT_TIMEZONE,
                                    RequestConstants.Examples.DATE_TIME_TIMEZONE);

                    log.warn(errMsg);
                    throw new IllegalArgumentException(errMsg);
                }

                return zonedDateTime;

            } catch (final DateTimeParseException e) {
                final var errMsg = """
                    Failed to parse datetime: '%s'. Expected format: '%s' \
                    (e.g., '%s')
                    """.formatted(
                                text,
                                RequestConstants.DateTimeFormats.DATE_TIME_FORMAT_TIMEZONE,
                                RequestConstants.Examples.DATE_TIME_TIMEZONE);
                log.error(errMsg, e);
                throw new IllegalArgumentException(errMsg, e);
            }
        }
    }
}
