package online.talkandtravel.exception.token;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

/**
 * Thrown to indicate that the token is not valid
 */
public class InvalidTokenException extends HttpException {

  private static final String MESSAGE = "Token %s is invalid";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public InvalidTokenException(String message, String messageToClient) {
    super(message, messageToClient, STATUS);
  }

  public InvalidTokenException(String token) {
    super(MESSAGE.formatted(token), STATUS);
  }

}
