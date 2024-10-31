package online.talkandtravel.exception.file;

import static online.talkandtravel.util.constants.FileFormatConstants.SUPPORTED_FORMAT_AVATAR;

import java.util.Arrays;
import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;
/**
 * Exception thrown when an image cannot be processed due to unsupported media type.
 */
public class UnsupportedImageFormatException extends HttpException {

    private static final String message = "Your photo must be in %s".formatted(Arrays.toString(SUPPORTED_FORMAT_AVATAR));

    private static final HttpStatus STATUS = HttpStatus.UNSUPPORTED_MEDIA_TYPE;

    public UnsupportedImageFormatException() {
        super(message, STATUS);
    }

}
