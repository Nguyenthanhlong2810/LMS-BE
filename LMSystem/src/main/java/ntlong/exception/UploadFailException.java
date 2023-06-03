package ntlong.exception;

public class UploadFailException extends RuntimeException{

    public UploadFailException(String msg) {
        super(msg);
    }

    public UploadFailException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
