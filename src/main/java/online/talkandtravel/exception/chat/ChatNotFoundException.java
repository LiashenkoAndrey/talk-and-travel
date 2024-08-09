package online.talkandtravel.exception.chat;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a chat is not found by its ID.
 *
 * <p>This exception is used to signal that the requested chat resource could not be found in the
 * system. It extends {@link ApiException} to integrate with the application's exception handling
 * framework, providing both a specific message and an HTTP status code for better clarity and
 * client communication.
 *
 * <p>The exception includes a message formatted with the chat ID that was not found and an HTTP
 * status of 404 (Not Found), which is appropriate for cases where the resource does not exist.
 */
public class ChatNotFoundException extends ApiException {

  private static final String MESSAGE = "Chat with id %s not found";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public ChatNotFoundException(Long chatId) {
    super(MESSAGE.formatted(chatId), STATUS);
  }
}
