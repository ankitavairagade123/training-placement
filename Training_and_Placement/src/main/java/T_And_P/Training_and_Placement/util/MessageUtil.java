package T_And_P.Training_and_Placement.util;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Reads user-facing text from messages.properties.
 * Callers throw PlacementApplicationException with the returned message.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageUtil {

    private final MessageSource messageSource;

    /**
     * Returns a message from messages.properties.
     */
    public String get(String key) {
        log.debug("get() started for key={}", key);
        return messageSource.getMessage(
                key,
                null,
                Locale.getDefault()
        );
    }

    /**
     * Returns a parameterized message from messages.properties.
     */
    public String get(String key, Object... args) {
        log.debug(
                "get() started for key={}, argsCount={}",
                key,
                args == null ? 0 : args.length
        );

        return messageSource.getMessage(
                key,
                args,
                Locale.getDefault()
        );
    }

    /**
     * Returns the message for the given key from messages.properties.
     */
    public String badRequest(String key) {
        log.info("badRequest() started for key={}", key);
        return get(key);
    }

    /**
     * Returns a parameterized message for the given key from messages.properties.
     */
    public String badRequest(String key, Object arg) {
        log.info("badRequest() started for key={}", key);
        return get(key, arg);
    }
}