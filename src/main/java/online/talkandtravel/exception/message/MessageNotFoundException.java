package online.talkandtravel.exception.message;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;
/**
 * Exception thrown when a specified message is not found.
 */
public class MessageNotFoundException extends ApiException {

  private static final String MESSAGE = "Message with id %s not found";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public MessageNotFoundException(Long messageId) {
    super(String.format(MESSAGE, messageId), STATUS);
  }
}
