package T_And_P.Training_and_Placement.exception;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Converts PlacementApplicationException and bean-validation errors
 * into a consistent API body.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles @Valid / @NotBlank failures from request DTOs.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        log.info("handleMethodArgumentNotValidException() started");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value());

        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));

        body.put("message", errors);

        log.info(
                "handleMethodArgumentNotValidException() completed, message={}",
                errors
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all business exceptions thrown from services.
     */
    @ExceptionHandler(PlacementApplicationException.class)
    protected ResponseEntity<?> handlePlacementException(
            PlacementApplicationException ex,
            WebRequest request) {

        log.info(
                "handlePlacementException() started, message={}",
                ex.getMessage()
        );

        return build(ex.getStatus(), ex.getMessage());
    }

    /**
     * Handles unexpected runtime errors that were not converted
     * to PlacementApplicationException.
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleUnexpectedException(
            Exception ex,
            WebRequest request) {

        log.error(
                "handleUnexpectedException() started, message={}",
                ex.getMessage(),
                ex
        );

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again."
        );
    }

    /**
     * Common error JSON: timestamp, status, message.
     */
    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status,
            String message) {

        log.info(
                "build() started for status={}, message={}",
                status,
                message
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("message", message);

        log.info("build() completed for status={}", status);

        return new ResponseEntity<>(body, status);
    }
}