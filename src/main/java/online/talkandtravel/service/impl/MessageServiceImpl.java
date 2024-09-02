package online.talkandtravel.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.message.MessageNotFoundException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.enums.MessageType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.service.AuthenticationService;
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
@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;
  private final UserChatRepository userChatRepository;
  private final MessageMapper messageMapper;
  private final AuthenticationService authenticationService;

  @Override
  public MessageDto saveMessage(SendMessageRequest request) {
    User sender = authenticationService.getAuthenticatedUser();
    checkUserJoinedTheChat(request, sender.getId());
    Chat chat = getChat(request, sender.getId());
    Message repliedMessage = getMessage(request, sender.getId());

    Message message =
        Message.builder()
            .type(MessageType.TEXT)
            .content(request.content())
            .repliedMessage(repliedMessage)
            .sender(sender)
            .build();
    chat.getMessages().add(message);
    chat = chatRepository.save(chat);
    // Retrieve the last added message from the saved chat
    message = chat.getMessages().get(chat.getMessages().size() - 1);
    message.setChat(chat);
    return messageMapper.toMessageDto(message);
  }

  private void checkUserJoinedTheChat(SendMessageRequest request, Long senderId) {
    userChatRepository.findByChatIdAndUserId(request.chatId(), senderId)
            .orElseThrow(() -> new UserNotJoinedTheChatException(senderId, request.chatId()));
  }

  private Chat getChat(SendMessageRequest request,  Long senderId) {
    try {
      return chatRepository
          .findById(request.chatId())
          .orElseThrow(() -> new ChatNotFoundException(request.chatId()));
    } catch (ChatNotFoundException e) {
      log.error("chat not found caught");
      throw new WebSocketException(e, senderId);
    }
  }

  private Message getMessage(SendMessageRequest request,  Long senderId) {
    Long messageId = request.repliedMessageId();
    if (messageId == null) {
      return null;
    }
    try {
      return messageRepository
          .findById(messageId)
          .orElseThrow(() -> new MessageNotFoundException(messageId));
    } catch (MessageNotFoundException e) {
      throw new WebSocketException(e, senderId);
    }
  }
}
