package online.talkandtravel.exception.chat;

import java.util.List;
import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a private chat already exists between two users.
 *
 * @param participantIds List of users who are part of the existing private chat.
 */
public class PrivateChatAlreadyExistsException extends ApiException {

  private static final String MESSAGE = "Private chat already exists for users with ids: %s and %s";
  private static final HttpStatus STATUS = HttpStatus.CONFLICT;

  public PrivateChatAlreadyExistsException(List<Long> participantIds) {
    super(MESSAGE.formatted(participantIds.get(0), participantIds.get(1)), STATUS);
  }
}
