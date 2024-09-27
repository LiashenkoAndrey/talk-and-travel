package online.talkandtravel.util;

import online.talkandtravel.exception.user.UserChatNotFoundException;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.repository.UserChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestChatService {

  @Autowired private UserChatRepository userChatRepository;

  public void setLastReadMessageId(Long chatId, Long userId, Long lastReadMessageId) {
    UserChat userChat =
        userChatRepository
            .findByChatIdAndUserId(chatId, userId)
            .orElseThrow(() -> new UserChatNotFoundException(chatId, userId));
    userChat.setLastReadMessageId(lastReadMessageId);
    userChatRepository.save(userChat);
  }
}