package ntlong.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseMessage {
    private int status;
    private String code;
    private String message;
    private Object data;
    private Long total;

    public ResponseMessage(int responseStatus, String responseCode, String message) {
        super();
        this.status = responseStatus;
        this.code = responseCode;
        this.message = message;
    }

    public ResponseMessage(String message) {
        super();
        this.message = message;
    }

    public ResponseMessage(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public ResponseMessage(int responseStatus, String message, Object data, Long total) {
        super();
        this.status = responseStatus;
        this.message = message;
        this.data = data;
        this.total = total;
    }
}
