package online.talkandtravel.exception;

public class FileSizeExceededException extends ApiException{

    public FileSizeExceededException() {
    }

    public FileSizeExceededException(String message) {
        super(message);
    }

    public FileSizeExceededException(String message, String messageToClient) {
        super(message, messageToClient);
    }

    public FileSizeExceededException(String message, boolean hideMessageFromClient) {
        super(message, hideMessageFromClient);
    }

    public FileSizeExceededException(Throwable cause) {
        super(cause);
    }
}
