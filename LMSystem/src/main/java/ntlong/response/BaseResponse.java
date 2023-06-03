package ntlong.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse {
    private int responseStatus;
    private String message;
    private Object data;


    public BaseResponse(int responseStatus, String message) {
        super();
        this.responseStatus = responseStatus;
        this.message = message;
    }

    public BaseResponse(String message) {
        super();
        this.message = message;
    }

    public BaseResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public BaseResponse(int responseStatus, String message, Object data) {
        super();
        this.responseStatus = responseStatus;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(int value, String message, String s, String description) {
        this.responseStatus = value;
        this.message = message;
    }
}

