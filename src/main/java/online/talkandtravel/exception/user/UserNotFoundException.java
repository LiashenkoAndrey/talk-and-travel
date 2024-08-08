package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.ApiException;

public class UserNotFoundException extends ApiException {

  private static final String MESSAGE = "User with id %s not found";

  public UserNotFoundException(String message, String messageToClient) {
    super(message, messageToClient);
  }

  public UserNotFoundException(Long userId) {
    super(MESSAGE.formatted(userId));
  }
}
