package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.ApiException;

public class UserAlreadyJoinTheChatException extends ApiException {

  private static final String MESSAGE = "User with id %s already JOINED the chat %s";

  public UserAlreadyJoinTheChatException(Long userId, Long chatId) {
    super(String.format(MESSAGE, userId, chatId));
  }
}
