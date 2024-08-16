package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.WebSocketException;
import org.springframework.http.HttpStatus;

public class UserAlreadyJoinTheChatException extends WebSocketException {

  private static final String MESSAGE = "User with id %s already JOINED the chat %s";
  private static final HttpStatus STATUS = HttpStatus.CONFLICT;

  public UserAlreadyJoinTheChatException(Long userId, Long chatId) {
    super(String.format(MESSAGE, userId, chatId), STATUS, userId);
  }
}
