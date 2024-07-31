package online.talkandtravel.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadySubscribedException extends ApiException{
    public UserAlreadySubscribedException() {
    }

    public UserAlreadySubscribedException(String message) {
        super(message);
    }

    public UserAlreadySubscribedException(String message, String messageToClient, HttpStatus httpStatus) {
        super(message, messageToClient, httpStatus);
    }

    public UserAlreadySubscribedException(String message, String messageToClient) {
        super(message, messageToClient);
    }

    public UserAlreadySubscribedException(String message, boolean hideMessageFromClient) {
        super(message, hideMessageFromClient);
    }

    public UserAlreadySubscribedException(Throwable cause) {
        super(cause);
    }
}
