package br.com.gorillaroxo.sanjy.client.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.aot.hint.annotation.Reflective;

/**
 * Validator that checks if a URL's hostname contains underscores. Underscores in hostnames are not allowed per RFC
 * 952/1123 and will cause the JDK HttpClient to throw {@link IllegalArgumentException}.
 */
@Reflective
public class NoUnderscoreInHostnameValidator implements ConstraintValidator<NoUnderscoreInHostname, String> {

    // Pattern to extract hostname from URL: scheme://[user@]hostname[:port][/path]
    private static final Pattern HOST_PATTERN = Pattern.compile("^\\w+://(?:[^@]+@)?([^/:]+)");

    @Override
    public boolean isValid(final String url, final ConstraintValidatorContext context) {
        if (url == null || url.isBlank()) {
            return true; // Let @NotNull or @NotBlank handle null/blank validation
        }

        final Matcher matcher = HOST_PATTERN.matcher(url);
        if (matcher.find()) {
            final String host = matcher.group(1);
            return !host.contains("_");
        }

        // If pattern doesn't match, let @URL validation handle it
        return true;
    }
}
