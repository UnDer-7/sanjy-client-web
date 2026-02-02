package br.com.gorillaroxo.sanjy.client.web.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Validates that a URL does not contain underscores in the hostname.
 * The JDK HttpClient (used by Spring's RestClient) rejects URIs with underscores
 * in the hostname as they violate RFC 952/1123.
 */
@Documented
@Constraint(validatedBy = NoUnderscoreInHostnameValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface NoUnderscoreInHostname {

    String message() default "URL hostname contains underscore which is not supported by JDK HttpClient (RFC 952/1123). "
            + "Use hyphens (-) instead of underscores (_) in hostnames. "
            + "Example: use 'my-service' instead of 'my_service'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
