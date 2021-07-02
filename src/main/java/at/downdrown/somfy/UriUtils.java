package at.downdrown.somfy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * Utility class for easier URL handling.
 *
 * @author Manfred Huber
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class UriUtils {

    /**
     * Extracts the URL parameters from a given {@link URI}.
     *
     * @param uri the URI which parameters shall be extracted.
     * @return a {@link Map} of the URI parameters and values.
     */
    public static Map<String, List<String>> getQueryParams(final URI uri) {
        if (uri == null || uri.getQuery() == null) {
            return Collections.emptyMap();
        }
        return Arrays.stream(uri.getQuery().split("&"))
            .map(UriUtils::splitQueryParameter)
            .collect(
                groupingBy(
                    AbstractMap.SimpleImmutableEntry::getKey,
                    LinkedHashMap::new,
                    mapping(Map.Entry::getValue, toList())));
    }

    private static AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(final String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : "";
        return new AbstractMap.SimpleImmutableEntry<>(
            URLDecoder.decode(key, StandardCharsets.UTF_8),
            URLDecoder.decode(value, StandardCharsets.UTF_8)
        );
    }
}
