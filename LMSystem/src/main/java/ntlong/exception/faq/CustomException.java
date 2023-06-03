package ntlong.exception.faq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomException extends RuntimeException{
    private HttpStatus httpCode;
    private String code;
    private String errorDetail;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, HttpStatus httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public CustomException(String message, HttpStatus httpCode, String code) {
        super(message);
        this.httpCode = httpCode;
        this.code = code;
    }

    public CustomException(String message, HttpStatus httpCode, String code, String errorDetail) {
        super(message);
        this.httpCode = httpCode;
        this.code = code;
        this.errorDetail = errorDetail;
    }
}
