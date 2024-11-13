package online.talkandtravel.exception.file;

import static online.talkandtravel.util.constants.FileFormatConstants.SUPPORTED_FORMAT_AVATAR;

import java.util.Arrays;
import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;
/**
 * Exception thrown when an image cannot be processed due to unsupported media type.
 */
public class UnsupportedImageFormatException extends HttpException {

    private static final String message = "Your photo must be in %s";
    private static final String messageWithProvidedImageType = "Your photo type is: %s but must be in %s";

    private static final HttpStatus STATUS = HttpStatus.UNSUPPORTED_MEDIA_TYPE;

    public UnsupportedImageFormatException() {
        super(message.formatted(Arrays.toString(SUPPORTED_FORMAT_AVATAR)), STATUS);
    }

    public UnsupportedImageFormatException(String imageType, String[] supportedFormats) {
        super(message.formatted(Arrays.asList(supportedFormats)), STATUS);
    }

}
