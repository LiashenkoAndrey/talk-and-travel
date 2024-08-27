package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends HttpException {
  private static final String MESSAGE = "A user with email %s already exists";
  private static final HttpStatus STATUS = HttpStatus.CONFLICT;

  public UserAlreadyExistsException(String email) {
    super(String.format(MESSAGE, email), STATUS);
  }
}
