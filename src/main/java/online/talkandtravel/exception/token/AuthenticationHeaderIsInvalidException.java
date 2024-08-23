package online.talkandtravel.exception.token;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

/**
 * Thrown to indicate that the Authorization header in the HTTP request is invalid.
 * This typically occurs when the header does not start with "Bearer ".
 */
public class AuthenticationHeaderIsInvalidException extends HttpException {

  private static final String MESSAGE = "Authentication header '%s' is invalid";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public AuthenticationHeaderIsInvalidException(String message, String messageToClient) {
    super(message, messageToClient, STATUS);
  }

  public AuthenticationHeaderIsInvalidException(String header) {
    super(MESSAGE.formatted(header), STATUS);
  }

}
