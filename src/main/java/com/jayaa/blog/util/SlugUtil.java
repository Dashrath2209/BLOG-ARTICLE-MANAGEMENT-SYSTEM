package com.jayaa.blog.util;

import org.springframework.stereotype.Component;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class SlugUtil {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Convert to lowercase
        String slug = input.toLowerCase(Locale.ENGLISH);

        // Remove accents
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{M}", "");

        // Replace spaces with hyphens
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // Remove non-latin characters except hyphens
        slug = NON_LATIN.matcher(slug).replaceAll("");

        // Remove multiple hyphens
        slug = slug.replaceAll("-+", "-");

        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }

    // Generate unique slug by appending number if needed
    public String generateUniqueSlug(String input, int attempt) {
        String baseSlug = generateSlug(input);
        return attempt == 0 ? baseSlug : baseSlug + "-" + attempt;
    }
}