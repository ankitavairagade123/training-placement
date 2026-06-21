package T_And_P.Training_and_Placement.exception;


import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        //Get all errors
        String errors = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        body.put("message", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value = {CompanyException.class})
    protected ResponseEntity<?> handleException(CompanyException ux, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", ux.getHttpStatus().value());
        body.put("message", ux.getMessage());
        return new ResponseEntity<>(body, ux.getHttpStatus());
    }

}
