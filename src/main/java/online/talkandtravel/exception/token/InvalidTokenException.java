package online.talkandtravel.exception.token;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

/**
 * Thrown to indicate that the token is not valid
 */
public class InvalidTokenException extends HttpException {

  private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

  public InvalidTokenException(String message, String messageToClient) {
    super(message, messageToClient, STATUS);
  }

}
