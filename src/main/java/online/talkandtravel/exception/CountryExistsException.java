package online.talkandtravel.exception;

import org.springframework.http.HttpStatus;

public class CountryExistsException extends ApiException {
    public CountryExistsException() {
    }

    public CountryExistsException(String message) {
        super(message);
    }

    public CountryExistsException(String message, String messageToClient, HttpStatus httpStatus) {
        super(message, messageToClient, httpStatus);
    }

    public CountryExistsException(String message, String messageToClient) {
        super(message, messageToClient);
    }

    public CountryExistsException(String message, boolean hideMessageFromClient) {
        super(message, hideMessageFromClient);
    }

    public CountryExistsException(Throwable cause) {
        super(cause);
    }
}
