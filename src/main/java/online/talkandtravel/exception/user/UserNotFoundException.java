package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {

  public UserNotFoundException() {
  }

  private static final String MESSAGE = "User with id %s not found";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public UserNotFoundException(String message, String messageToClient) {
    super(message, messageToClient, STATUS);
  }

  public UserNotFoundException(Long userId) {
    super(MESSAGE.formatted(userId), STATUS);
  }
}
