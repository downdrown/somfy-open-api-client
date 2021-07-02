package at.downdrown.somfy.exception;

/**
 * Base class for all library specific exceptions.
 *
 * @author Manfred Huber
 */
public abstract class SomfyClientException extends Exception {

    public SomfyClientException(String message) {
        super(message);
    }

    public SomfyClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
