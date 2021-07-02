package at.downdrown.somfy.exception;

import lombok.Getter;
import lombok.ToString;

import java.net.URI;

/**
 * Exception that indicates that a request to Somfy's Open API has not been successful.
 *
 * @author Manfred Huber
 */
@Getter
@ToString
public class SomfyApiRequestException extends RuntimeException{

    private final URI requestedUri;
    private final int httpStatus;

    public SomfyApiRequestException(String message, URI requestedUri, int httpStatus) {
        this(message, null, requestedUri, httpStatus);
    }

    public SomfyApiRequestException(String message, Throwable cause, URI requestedUri, int httpStatus) {
        super(message, cause);
        this.requestedUri = requestedUri;
        this.httpStatus = httpStatus;
    }
}
