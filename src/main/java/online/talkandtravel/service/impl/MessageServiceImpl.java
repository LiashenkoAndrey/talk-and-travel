package online.talkandtravel.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.message.MessageNotFoundException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.MessageService;
import online.talkandtravel.util.mapper.MessageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link MessageService} for handling message operations within chats.
 *
 * <p>This service provides methods for saving messages to a chat and retrieving required entities
 * such as the chat, user, and optionally a replied message.
 *
 * <p>The service includes the following functionalities:
 *
 * <ul>
 *   <li>{@link #saveMessage(SendMessageRequest)} - Saves a new message to a specified chat. It
 *       handles adding the message to the chat, associating it with a user, and linking it to a
 *       replied message if provided.
 * </ul>
 */
@Transactional
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;
  private final UserRepository userRepository;
  private final UserChatRepository userChatRepository;
  private final MessageMapper messageMapper;

  @Override
  public MessageDtoBasic saveMessage(SendMessageRequest request) {
    checkUserJoinedTheChat(request);

    Chat chat = getChat(request);

    Message repliedMessage = getMessage(request);

    User sender = getUser(request);

    Message message =
        Message.builder()
            .content(request.content())
            .repliedMessage(repliedMessage)
            .sender(sender)
            .build();
    chat.getMessages().add(message);
    chat = chatRepository.save(chat);
    // Retrieve the last added message from the saved chat
    message = chat.getMessages().get(chat.getMessages().size() - 1);
    return messageMapper.toMessageDtoBasic(message);
  }

  private void checkUserJoinedTheChat(SendMessageRequest request) {
    Optional<UserChat> optionalUserChat = userChatRepository.findByChatIdAndUserId(request.chatId(),
        request.senderId());
    if(optionalUserChat.isEmpty()){
      throw new UserNotJoinedTheChatException(request.senderId(), request.chatId());
    }
  }

  private Chat getChat(SendMessageRequest request) {
    return chatRepository
        .findById(request.chatId())
        .orElseThrow(() -> new ChatNotFoundException(request.chatId()));
  }

  private User getUser(SendMessageRequest request) {
    return userRepository
        .findById(request.senderId())
        .orElseThrow(() -> new UserNotFoundException(request.senderId()));
  }

  private Message getMessage(SendMessageRequest request) {
    Long messageId = request.repliedMessageId();
    if (messageId == null) {
      return null;
    }
    return messageRepository
        .findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(messageId));
  }
}
