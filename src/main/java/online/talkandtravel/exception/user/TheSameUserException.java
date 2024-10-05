package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

public class TheSameUserException extends HttpException {
  private static final String MESSAGE = "Creation a chat with the same user. User id: %s";
  private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

  public TheSameUserException(Long userId) {
    super(String.format(MESSAGE, userId), STATUS);
  }
}
