package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.exception.TimezoneInvalidException;
import br.com.gorillaroxo.sanjy.client.web.exception.TimezoneNotProvidedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to handle timezone conversions between user timezone and UTC. The sanjy-server expects all datetime fields to
 * be in UTC.
 */
@Slf4j
@Service
public class TimezoneConversionService {

    /**
     * Convert a LocalDateTime from user's timezone to UTC Instant. This is used when the user submits a datetime (e.g.,
     * meal consumed time) in their local timezone, and we need to convert it to UTC before sending to the backend.
     *
     * @param localDateTime the datetime in user's timezone
     * @param userTimezone the user's timezone (required)
     * @return the equivalent Instant in UTC
     * @throws TimezoneNotProvidedException if userTimezone is null
     */
    public Instant convertToUTC(final LocalDateTime localDateTime, final String userTimezone) {
        if (localDateTime == null) {
            return null;
        }

        if (userTimezone == null) {
            throw new TimezoneNotProvidedException("Timezone is required for datetime conversion");
        }

        final ZoneId userZoneId = parseTimezone(userTimezone);

        // Interpret the LocalDateTime as being in the user's timezone
        ZonedDateTime zonedDateTime = localDateTime.atZone(userZoneId);

        // Convert to Instant (UTC)
        Instant utcInstant = zonedDateTime.toInstant();

        log.debug(
                "Converted LocalDateTime {} in timezone {} to UTC Instant {}", localDateTime, userTimezone, utcInstant);

        return utcInstant;
    }

    /**
     * Convert a UTC Instant to LocalDateTime in user's timezone. This is used when displaying datetimes from the
     * backend to the user.
     *
     * @param instant the UTC instant
     * @param userTimezone the user's timezone (required)
     * @return the equivalent LocalDateTime in user's timezone
     * @throws TimezoneNotProvidedException if userTimezone is null
     */
    public LocalDateTime convertFromUTC(final Instant instant, final String userTimezone) {
        if (instant == null) {
            return null;
        }

        if (userTimezone == null) {
            throw new TimezoneNotProvidedException("Timezone is required for datetime conversion");
        }

        final ZoneId userZoneId = parseTimezone(userTimezone);

        // Convert Instant to ZonedDateTime in user's timezone
        ZonedDateTime zonedDateTime = instant.atZone(userZoneId);

        // Extract LocalDateTime
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

        log.debug("Converted UTC Instant {} to LocalDateTime {} in timezone {}", instant, localDateTime, userTimezone);

        return localDateTime;
    }

    /**
     * Parse timezone string to ZoneId. Throws appropriate exceptions if timezone is missing or invalid.
     *
     * @param userTimezone the timezone string from request
     * @return the parsed ZoneId
     * @throws TimezoneNotProvidedException if timezone is null or empty
     * @throws TimezoneInvalidException if timezone is not valid
     */
    private ZoneId parseTimezone(final String userTimezone) {
        if (userTimezone == null || userTimezone.isEmpty()) {
            throw new TimezoneNotProvidedException("User timezone must be provided");
        }

        try {
            return ZoneId.of(userTimezone);
        } catch (final Exception e) {
            throw new TimezoneInvalidException("Invalid timezone: " + userTimezone, e);
        }
    }
}
