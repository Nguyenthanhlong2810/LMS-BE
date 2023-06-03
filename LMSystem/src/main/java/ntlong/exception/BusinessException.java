package ntlong.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus code;

    public BusinessException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }
}
