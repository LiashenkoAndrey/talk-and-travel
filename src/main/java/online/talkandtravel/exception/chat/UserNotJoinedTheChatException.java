package online.talkandtravel.exception.chat;

import online.talkandtravel.exception.model.ApiException;

public class UserNotJoinedTheChatException extends ApiException {

  private static final String MESSAGE = "User %s is not joined the chat %s";

  public UserNotJoinedTheChatException(Long userId, Long chatId) {
    super(String.format(MESSAGE, userId, chatId));
  }
}
