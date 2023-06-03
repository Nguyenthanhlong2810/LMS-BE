package ntlong.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

public class ResponseModelBase<T> {
    private boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String messages;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer errorCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseModelBase(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public ResponseModelBase(boolean success, String messages) {
        this.success = success;
        this.messages = messages;
    }

    public ResponseModelBase(boolean success, String messages, Integer errorCode) {
        this.success = success;
        this.messages = messages;
        this.errorCode = errorCode;
    }

    private ResponseModelBase(boolean success, String messages, T data) {
        this.success = success;
        this.messages = messages;
        this.data = data;
    }

    private ResponseModelBase(boolean success, String messages, Integer errorCode, T data) {
        this.success = success;
        this.messages = messages;
        this.errorCode = errorCode;
        this.data = data;
    }

    public static <T> ResponseModelBase<T> succeeded(T data) {
        return new ResponseModelBase<>(true, data);
    }

    public static <T> ResponseModelBase<T> succeeded(String message) {
        return new ResponseModelBase<>(true, message);
    }

    public static <T> ResponseModelBase<T> failed(Exception ex) {
        return new ResponseModelBase<>(false, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static <T> ResponseModelBase<T> failed(String message, Integer errorCode) {
        return new ResponseModelBase<>(false, message, errorCode);
    }

    public static <T> ResponseModelBase<T> failed(T data, String message, Integer errorCode) {
        return new ResponseModelBase<>(false, message, errorCode, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
