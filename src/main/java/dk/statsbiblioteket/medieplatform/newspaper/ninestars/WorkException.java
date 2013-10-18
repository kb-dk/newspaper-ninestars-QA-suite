package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

/**
 * This exception is used as a wrapper for the various exceptions that a component can throw when working
 */
public class WorkException extends Exception {

    public WorkException() {
    }

    public WorkException(String message) {
        super(message);
    }

    public WorkException(String message,
                         Throwable cause) {
        super(message, cause);
    }

    public WorkException(Throwable cause) {
        super(cause);
    }

    public WorkException(String message,
                         Throwable cause,
                         boolean enableSuppression,
                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
