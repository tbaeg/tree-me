package tree.me.service.exception;

public class TreeMeDeleteException extends RuntimeException {

    public TreeMeDeleteException() {
        super();
    }

    public TreeMeDeleteException(String message) {
        super(message);
    }

    public TreeMeDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public TreeMeDeleteException(Throwable cause) {
        super(cause);
    }

    protected TreeMeDeleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
