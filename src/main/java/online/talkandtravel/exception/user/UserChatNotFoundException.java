package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;

public class UserChatNotFoundException extends ApiException {
  private static final String MESSAGE_WHEN_FIND_BY_CHAT_ID_AND_USER_ID = "UserChat record where chat id:%s and userId:%s not found";
  private static final String MESSAGE_WHEN_NOT_FOUND_BY_ID = "UserChat with id %s not found";

  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public UserChatNotFoundException(Long chatId, Long userId) {
    super(MESSAGE_WHEN_FIND_BY_CHAT_ID_AND_USER_ID.formatted(chatId, userId), STATUS);
  }

  public UserChatNotFoundException(Long userChatId) {
    super(MESSAGE_WHEN_NOT_FOUND_BY_ID.formatted(userChatId), STATUS);
  }
}
