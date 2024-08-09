package online.talkandtravel.exception.file;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;
/**
 * Exception thrown when an image cannot be processed due to unsupported media type.
 */
public class UnsupportedFormatException extends ApiException {

    private static final HttpStatus STATUS = HttpStatus.UNSUPPORTED_MEDIA_TYPE;

    public UnsupportedFormatException(String message) {
        super(message, STATUS);
    }

}
