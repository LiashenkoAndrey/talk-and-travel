package online.talkandtravel.exception;

public class ImageProcessingException extends ApiException{

    public ImageProcessingException() {
    }

    public ImageProcessingException(String message) {
        super(message);
    }

    public ImageProcessingException(String message, String messageToClient) {
        super(message, messageToClient);
    }

    public ImageProcessingException(String message, boolean hideMessageFromClient) {
        super(message, hideMessageFromClient);
    }

    public ImageProcessingException(Throwable cause) {
        super(cause);
    }
}
