package at.downdrown.somfy.exception;

/**
 * Exception that indicates that no token could be fetched from Somfy.
 *
 * @author Manfred Huber
 */
public class SomfyTokenFetchException extends SomfyClientException {

    public SomfyTokenFetchException(String message) {
        super(message);
    }

    public SomfyTokenFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
