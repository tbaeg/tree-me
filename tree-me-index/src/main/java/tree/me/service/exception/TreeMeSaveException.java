package tree.me.service.exception;

public class TreeMeSaveException extends RuntimeException {

    public TreeMeSaveException() {
        super();
    }

    public TreeMeSaveException(String message) {
        super(message);
    }

    public TreeMeSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public TreeMeSaveException(Throwable cause) {
        super(cause);
    }

    protected TreeMeSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
