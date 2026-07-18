package T_And_P.Training_and_Placement.exception;

import org.springframework.http.HttpStatus;

public class PlacementApplicationException extends RuntimeException {

    private final HttpStatus status;

    public PlacementApplicationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}