package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

public class UserTokenNotFoundException extends HttpException {

  private static final String MESSAGE = "token of user with email %s not found";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public UserTokenNotFoundException(String message, String messageToClient) {
    super(message, messageToClient, STATUS);
  }

  public UserTokenNotFoundException(String email) {
    super(MESSAGE.formatted(email), STATUS);
  }
}
