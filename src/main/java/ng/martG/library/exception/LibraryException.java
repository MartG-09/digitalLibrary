package ng.martG.library.exception;

/** A business-rule problem whose message is safe to show to the user. */
public class LibraryException extends RuntimeException {
    public LibraryException(String message) {
        super(message);
    }
}
