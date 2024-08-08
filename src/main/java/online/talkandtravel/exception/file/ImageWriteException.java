package online.talkandtravel.exception.file;

import online.talkandtravel.exception.model.ApiException;

public class ImageWriteException extends ApiException {

    public ImageWriteException() {
    }

    public ImageWriteException(String message) {
        super(message);
    }

    public ImageWriteException(String message, String messageToClient) {
        super(message, messageToClient);
    }

    public ImageWriteException(String message, boolean hideMessageFromClient) {
        super(message, hideMessageFromClient);
    }

    public ImageWriteException(Throwable cause) {
        super(cause);
    }
}