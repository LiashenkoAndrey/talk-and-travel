package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

/** Exception thrown when a specified UserCountry is not found. */
public class UserCountryNotFoundException extends HttpException {

  private static final String MESSAGE =
      "UserCountry connection not exists for user %s and Country: %s";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public UserCountryNotFoundException(String name, Long id) {
    super(String.format(MESSAGE, id, name), STATUS);
  }
}
