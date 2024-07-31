package online.talkandtravel.exception;

public class AuthenticationException extends ApiException {

    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, String messageToClient) {
        super(message, messageToClient);
    }

    public AuthenticationException(String message, boolean hideMessageFromClient) {
        super(message, hideMessageFromClient);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}