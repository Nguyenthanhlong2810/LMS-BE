package ntlong.exception;

public class NewsNotExistsException extends Throwable{

    public NewsNotExistsException(String msg) {
        super(msg);
    }

    public NewsNotExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
