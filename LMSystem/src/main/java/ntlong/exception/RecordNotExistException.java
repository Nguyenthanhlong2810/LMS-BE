package ntlong.exception;

public class RecordNotExistException extends Throwable{

    public RecordNotExistException(String msg) {
        super(msg);
    }

    public RecordNotExistException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
