package online.talkandtravel.service.impl;

import lombok.RequiredArgsConstructor;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.message.MessageNotFoundException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.MessageService;
import online.talkandtravel.util.mapper.MessageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;

  @Override
  public MessageDtoBasic saveMessage(SendMessageRequest request) {
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
