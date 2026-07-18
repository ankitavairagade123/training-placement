package T_And_P.Training_and_Placement.exception;

import org.springframework.http.HttpStatus;

public class StudentException extends RuntimeException{

    private HttpStatus status;

    public StudentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
