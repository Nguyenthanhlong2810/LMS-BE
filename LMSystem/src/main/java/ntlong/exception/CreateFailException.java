package ntlong.exception;

public class CreateFailException extends Throwable{

    public CreateFailException(String msg) {
        super(msg);
    }

    public CreateFailException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
