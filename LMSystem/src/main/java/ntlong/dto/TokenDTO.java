package ntlong.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenDTO {
    private String token;
    private String error;
    private HttpStatus httpStatus;
    private int httpCode;

    public TokenDTO(String token, String error) {
        this.token = token;
        this.error = error;
    }

    public TokenDTO(String error) {
        this.error = error;
    }
}
