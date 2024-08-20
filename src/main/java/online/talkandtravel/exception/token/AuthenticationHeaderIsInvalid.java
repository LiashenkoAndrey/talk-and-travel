package online.talkandtravel.exception.token;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

public class AuthenticationHeaderIsInvalid extends HttpException {

  private static final String MESSAGE = "Authentication header %s is invalid";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public AuthenticationHeaderIsInvalid(String message, String messageToClient) {
    super(message, messageToClient, STATUS);
  }

  public AuthenticationHeaderIsInvalid(String header) {
    super(MESSAGE.formatted(header), STATUS);
  }

}
