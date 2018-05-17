package tree.me.service.exception;

public class TreeMeGetException extends RuntimeException {

    public TreeMeGetException() {
        super();
    }

    public TreeMeGetException(String message) {
        super(message);
    }

    public TreeMeGetException(String message, Throwable cause) {
        super(message, cause);
    }

    public TreeMeGetException(Throwable cause) {
        super(cause);
    }

    protected TreeMeGetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
