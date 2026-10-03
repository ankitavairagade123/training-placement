package T_And_P.Training_and_Placement.util;

import org.springframework.util.StringUtils;

/**
 * Java 8 helpers for null-safe mapping between request DTO, entity and response DTO.
 */
public final class MapperUtil {

    private MapperUtil() {
    }

    /**
     * Trims text and converts blank values to null.
     */
    public static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    /**
     * Returns true when the value is null or only whitespace.
     */
    public static boolean isBlank(String value) {
        return !StringUtils.hasText(value);
    }

    /**
     * Parses an enum from a DB/API string. Returns null when the value is blank.
     */
    public static <E extends Enum<E>> E parseEnum(
            Class<E> type,
            String value) {

        if (type == null || !StringUtils.hasText(value)) {
            return null;
        }

        return Enum.valueOf(type, value.trim());
    }

    /**
     * Parses an enum and falls back to the given default when the value is blank.
     */
    public static <E extends Enum<E>> E parseEnum(
            Class<E> type,
            String value,
            E defaultValue) {

        E parsed = parseEnum(type, value);
        return parsed == null ? defaultValue : parsed;
    }

    /**
     * Converts a Long native-query value to Integer without NPE.
     */
    public static Integer toInteger(Long value) {
        return value == null ? null : value.intValue();
    }
}