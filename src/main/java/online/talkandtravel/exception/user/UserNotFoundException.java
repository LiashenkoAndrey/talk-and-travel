package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {

    public UserNotFoundException(String message, String messageToClient, HttpStatus httpStatus) {
        super(message, messageToClient, httpStatus);
    }

    public UserNotFoundException(String message, String messageToClient) {
        super(message, messageToClient);
    }

    public UserNotFoundException(String message, boolean hideMessageFromClient) {
        super(message, hideMessageFromClient);
    }

    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
