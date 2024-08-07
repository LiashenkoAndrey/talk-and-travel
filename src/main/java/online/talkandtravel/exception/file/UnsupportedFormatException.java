package online.talkandtravel.exception.file;

import online.talkandtravel.exception.model.ApiException;

public class UnsupportedFormatException extends ApiException {

    public UnsupportedFormatException(String message) {
        super(message);
    }

    public UnsupportedFormatException(String message, String messageToClient) {
        super(message, messageToClient);
    }

    public UnsupportedFormatException(String message, boolean hideMessageFromClient) {
        super(message, hideMessageFromClient);
    }

    public UnsupportedFormatException(Throwable cause) {
        super(cause);
    }
}
