package online.talkandtravel.util;

import online.talkandtravel.exception.message.MessageNotFoundException;
import online.talkandtravel.exception.user.UserChatNotFoundException;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestChatService {

  @Autowired private UserChatRepository userChatRepository;

  @Autowired private MessageRepository messageRepository;
  public void setLastReadMessageId(Long chatId, Long userId, Long lastReadMessageId) {
    UserChat userChat =
        userChatRepository
            .findByChatIdAndUserId(chatId, userId)
            .orElseThrow(() -> new UserChatNotFoundException(chatId, userId));
    if (lastReadMessageId != null) {
      userChat.setLastReadMessage(messageRepository.findById(lastReadMessageId).orElseThrow(() -> new MessageNotFoundException(lastReadMessageId)));
    }
    userChatRepository.save(userChat);
  }
}