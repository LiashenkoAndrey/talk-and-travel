package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends HttpException {

  private static final String BY_ID_MESSAGE = "User with id %s not found";
  private static final String BY_EMAIL_MESSAGE = "User with email %s not found";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public UserNotFoundException(String userEmail) {
    super(BY_EMAIL_MESSAGE.formatted(userEmail), STATUS);
  }

  public UserNotFoundException(Long userId) {
    super(BY_ID_MESSAGE.formatted(userId), STATUS);
  }
}
