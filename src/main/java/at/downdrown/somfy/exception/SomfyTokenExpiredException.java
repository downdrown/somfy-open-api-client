package at.downdrown.somfy.exception;

import at.downdrown.somfy.SomfyToken;

/**
 * Exception that indicates that a {@link SomfyToken} has completely expired.
 * Meaning that not only the {@code access_token} but also the {@code refresh_token}
 * has already expired.
 *
 * @author Manfred Huber
 */
public class SomfyTokenExpiredException extends SomfyClientException {

    public SomfyTokenExpiredException(String message) {
        super(message);
    }

    public SomfyTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
