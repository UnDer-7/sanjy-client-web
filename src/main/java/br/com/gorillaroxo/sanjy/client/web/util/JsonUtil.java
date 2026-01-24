package br.com.gorillaroxo.sanjy.client.web.util;

import static net.logstash.logback.argument.StructuredArguments.kv;

import br.com.gorillaroxo.sanjy.client.web.exception.DeserializationException;
import br.com.gorillaroxo.sanjy.client.web.exception.SerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    /**
     * Serialize Class to JSON
     *
     * <p>(convert object -> string)
     *
     * <p>If {@link JsonProcessingException} happens, an {@link SerializationException} is thrown
     *
     * <p>If you want to return null when serialization fails, then use {@link JsonUtil#serializeSafely(Object)}
     */
    public String serialize(final Object object) {
        Objects.requireNonNull(object, "object argument cannot be null");

        try {
            return objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            failSerializationLogging(e, object);
            throw new SerializationException(e);
        }
    }

    /**
     * Serialize Class to JSON
     *
     * <p>(convert object -> string)
     *
     * <p>If {@link JsonProcessingException} happens, an empty Optional ({@link Optional#empty()}) is return
     *
     * <p>If you want to throw an exception when serialization fails, then use {@link JsonUtil#serialize(Object)}
     */
    public Optional<String> serializeSafely(final Object object) {
        Objects.requireNonNull(object, "object argument cannot be null");

        try {
            return Optional.of(objectMapper.writeValueAsString(object));
        } catch (final JsonProcessingException e) {
            failSerializationLogging(e, object);
            return Optional.empty();
        }
    }

    /**
     * Deserialize JSON to the target Class
     *
     * <p>(convert string -> object)
     *
     * <p>If {@link JsonProcessingException} happens, an {@link DeserializationException} is thrown
     *
     * <p>If you want to return null when deserialization fails, then use {@link JsonUtil#deserializeSafely(String,
     * Class)}
     */
    public <T> T deserialize(final String json, final Class<T> clazz) {
        Objects.requireNonNull(json, "json argument cannot be null");
        Objects.requireNonNull(clazz, "clazz argument cannot be null");

        try {
            return objectMapper.readValue(json, clazz);
        } catch (final JsonProcessingException e) {
            failDeserializationLogging(e, json, clazz);
            throw new DeserializationException(e);
        }
    }

    /**
     * Deserialize JSON to the target Class
     *
     * <p>(convert string -> object)
     *
     * <p>If {@link JsonProcessingException} happens, an empty Optional ({@link Optional#empty()}) is return
     *
     * <p>If you want to throw a exception when deserialization fails, then use {@link JsonUtil#deserialize(String,
     * Class)}
     */
    public <T> Optional<T> deserializeSafely(final String json, final Class<T> clazz) {
        Objects.requireNonNull(json, "json argument cannot be null");
        Objects.requireNonNull(clazz, "clazz argument cannot be null");

        try {
            return Optional.of(objectMapper.readValue(json, clazz));
        } catch (final JsonProcessingException e) {
            failDeserializationLogging(e, json, clazz);
            return Optional.empty();
        }
    }

    private void failDeserializationLogging(final Throwable e, final String json, final Class<?> clazz) {
        log.warn(
                LogField.Placeholders.FOUR.placeholder,
                kv(LogField.MSG.label(), "Fail to deserialize JSON"),
                kv(LogField.JSON_DESERIALIZATION_SOURCE.label(), json),
                kv(LogField.CLASS_DESERIALIZATION_TARGET.label(), clazz.getSimpleName()),
                kv(LogField.EXCEPTION_MESSAGE.label(), e.getMessage()),
                e);
    }

    private void failSerializationLogging(final Throwable e, final Object object) {
        log.warn(
                LogField.Placeholders.THREE.placeholder,
                kv(LogField.MSG.label(), "Fail to serialize Class"),
                kv(LogField.CLASS_NAME.label(), object.getClass().getSimpleName()),
                kv(LogField.CLASS_SERIALIZATION_SOURCE.label(), object.toString()),
                kv(LogField.EXCEPTION_MESSAGE.label(), e.getMessage()),
                e);
    }
}
