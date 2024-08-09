package online.talkandtravel.exception.chat;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user is not joined to a specified chat.
 *
 * <p>This exception indicates that a user attempted to perform an action on a chat they are not a
 * member of. It extends {@link ApiException} to integrate with the application's exception handling
 * framework, providing a specific message and an HTTP status code to communicate the issue to the
 * client.
 *
 * <p>The exception message is formatted with the user ID and chat ID, providing context on which
 * user and chat are involved in the error. The HTTP status is set to 409 (Conflict) to reflect that
 * the user's action conflicts with the current state of the chat membership.
 */
public class UserNotJoinedTheChatException extends ApiException {

  private static final String MESSAGE = "User %s is not joined the chat %s";
  private static final HttpStatus STATUS = HttpStatus.CONFLICT;

  public UserNotJoinedTheChatException(Long userId, Long chatId) {
    super(String.format(MESSAGE, userId, chatId), STATUS);
  }
}
