package online.talkandtravel.exception.chat;

import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.model.dto.event.EventRequest;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a private chat does not contain exactly two users.
 *
 * <p>This exception is raised during WebSocket communication when an attempt is made to handle a
 * private chat that does not meet the requirement of containing two users. It includes the chat ID
 * and the author's ID from the event request.
 */
public class PrivateChatMustContainTwoUsersException extends WebSocketException {

  private static final String MESSAGE = "Chat with id %s is PRIVATE. It must contain two users.";
  private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

  public PrivateChatMustContainTwoUsersException(EventRequest request, Long authorId) {
    super(MESSAGE.formatted(request.chatId()), STATUS, authorId);
  }
}
