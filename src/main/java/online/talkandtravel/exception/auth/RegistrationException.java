package online.talkandtravel.exception.auth;

import online.talkandtravel.exception.model.ApiException;

public class RegistrationException extends ApiException {

    public RegistrationException() {
    }

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, String messageToClient) {
        super(message, messageToClient);
    }

    public RegistrationException(String message, boolean hideMessageFromClient) {
        super(message, hideMessageFromClient);
    }

    public RegistrationException(Throwable cause) {
        super(cause);
    }
}
