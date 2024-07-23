package online.talkandtravel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
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
