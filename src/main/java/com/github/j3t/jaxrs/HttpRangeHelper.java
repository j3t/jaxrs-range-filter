package com.github.j3t.jaxrs;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class helps with HTTP range headers.
 */
public final class HttpRangeHelper {
    // regex to extract values of a given HTTP range header
    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(\\d+)-(\\d+)?");

    private HttpRangeHelper() {
    }

    /**
     * Returns the from value of a given HTTP range header value.
     *
     * @param value HTTP range header value (e.g. bytes=6-10, bytes=100-, ...)
     * @return Long >= 0 or null if the given value is not a valid HTTP range.
     */
    public static Long getFromValue(String value) {
        if (value != null) {
            Matcher matcher = RANGE_PATTERN.matcher(value);

            if (matcher.matches()) {
                return Long.parseLong(matcher.group(1));
            }
        }

        return null;
    }

    /**
     * Returns the to value of a given HTTP range header value.
     *
     * @param value HTTP range header value (e.g. bytes=6-10, bytes=100-, ...)
     * @return Long >= 0 or null if to is not specified or the given value is not a valid HTTP range.
     */
    public static Long getToValue(String value) {
        if (value != null) {
            Matcher matcher = RANGE_PATTERN.matcher(value);

            if (matcher.matches() && matcher.group(2) != null) {
                return Long.parseLong(matcher.group(2));
            }
        }

        return null;
    }

    /**
     * Checks that a given HTTP range is satisfiable.
     *
     * @param value       HTTP range header value (e.g. bytes=6-10, bytes=100-, ...)
     * @param totalLength length of the entity (e.g. {@link File#length()})
     * @return true if the given HTTP range is valid and totalLength not exceeded, otherwise false.
     */
    public static boolean isSatisfiable(String value, long totalLength) {
        return isSatisfiable(getFromValue(value), getToValue(value), totalLength);
    }

    /**
     * Checks that a given HTTP range is satisfiable.
     *
     * @param from        HTTP range from value, must not be null >= 0 (e.g. 1, 6, 10, ...)
     * @param to          HTTP range to value, null or >= 0 (e.g. null, 1, 6, 10, ...)
     * @param totalLength length of the entity (e.g. {@link File#length()})
     * @return true if the given HTTP range is valid and totalLength not exceeded, otherwise false.
     */
    public static boolean isSatisfiable(Long from, Long to, long totalLength) {
        return from != null && from < totalLength && (to == null || to >= from && to < totalLength);
    }

    /**
     * Returns the length of a given HTTP range.
     *
     * @param from        HTTP range from value, must not be null >= 0 (e.g. 1, 6, 10, ...)
     * @param to          HTTP range to value, null or >= 0 (e.g. null, 1, 6, 10, ...)
     * @param totalLength length of the entity (e.g. {@link File#length()})
     * @return Long value >= 0 or null if to is not specified or the given value is not a valid HTTP range.
     */
    public static Long getLength(Long from, Long to, long totalLength) {
        if (isSatisfiable(from, to, totalLength)) {
            if (to != null) {
                return to - from + 1;
            } else {
                return totalLength - from;
            }
        }

        return null;
    }

    /**
     * Returns the length of a given HTTP range.
     *
     * @param from        HTTP range from value, must not be null >= 0 (e.g. 1, 6, 10, ...)
     * @param to          HTTP range to value, null or >= 0 (e.g. null, 1, 6, 10, ...)
     * @param totalLength length of the entity (e.g. {@link File#length()})
     * @param chunkSize   max length of the returned
     * @return Long value >= 0 or chunkSize if the range length greater than chunkSize or null if to is not specified
     * or the given value is not a valid HTTP range.
     */
    public static Long getLength(Long from, Long to, long totalLength, long chunkSize) {
        Long length = getLength(from, to, totalLength);

        return length == null ? null : length > chunkSize ? chunkSize : length;
    }

    /**
     * Creates a HTTP Content-Range value (e.g. bytes 6-10/26).
     *
     * @param offset      index of the byte the stream started from (e.g. 6)
     * @param length      size of the bytes written to the stream (e.g. 5)
     * @param totalLength size of the content at all (e.g. 26)
     * @return String, should not be null
     */
    public static String getContentRange(long offset, long length, long totalLength) {
        return String.format("bytes %s-%s/%s", offset, offset + length - 1, totalLength);
    }
}
