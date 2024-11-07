package online.talkandtravel.exception.token;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends HttpException {
  private static final String MESSAGE = "Token with id: %s is expired";
  private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

  public ExpiredTokenException(Long tokenId) {
    super(MESSAGE.formatted(tokenId), STATUS);
  }
}
