package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;

/** Exception thrown when a user not authenticated. */
public class UserNotAuthenticatedException extends ApiException {

  private static final String MESSAGE = "The user is not authenticated";
  private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

  public UserNotAuthenticatedException(String message) {
    super(message);
  }

  public UserNotAuthenticatedException(String message, String messageToClient) {
    super(message, messageToClient, STATUS);
  }

  public UserNotAuthenticatedException() {
    super(MESSAGE, STATUS);
  }
}
