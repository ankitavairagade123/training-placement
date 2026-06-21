package T.P.example.Training_and_Placement.exception;
import org.springframework.http.HttpStatus;

public class CompanyException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public CompanyException(String message, String id) {
        super(message);
    }

    public CompanyException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
