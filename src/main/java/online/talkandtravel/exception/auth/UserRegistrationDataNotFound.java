package online.talkandtravel.exception.auth;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

public class UserRegistrationDataNotFound extends HttpException {
  private static final String MESSAGE = "User registration data is not found in temp storage";
  private static final String MESSAGE_TO_CLIENT = "User not found";

  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public UserRegistrationDataNotFound() {
    super(MESSAGE, MESSAGE_TO_CLIENT, STATUS);
  }
}
