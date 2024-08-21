package online.talkandtravel.service.impl.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.MessageType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.impl.MessageServiceImpl;
import online.talkandtravel.util.mapper.MessageMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

  @InjectMocks private MessageServiceImpl underTest;

  @Mock private MessageRepository messageRepository;

  @Mock private ChatRepository chatRepository;

  @Mock private UserRepository userRepository;

  @Mock private UserChatRepository userChatRepository;

  @Mock private MessageMapper messageMapper;

  private final Long chatId = 1L;
  private final Long userId = 1L;


  @Test
  void saveMessage_shouldReturnMessageDtoBasic_whenUserJoinedChatAndMessageExists() {
    // Arrange

    Long repliedMessageId = 2L;
    String content = "Hello, World!";
    SendMessageRequest request = new SendMessageRequest(content, chatId, userId, repliedMessageId);
    Chat chat = new Chat();
    User user = new User();
    Message repliedMessage = new Message();
    Message message = new Message();
    chat.getMessages().add(message);
    UserNameDto userNameDto = new UserNameDto(1L, "userName");
    MessageDto messageDto =
        new MessageDto(
                    1L,
                    MessageType.TEXT,
                    "",
                    LocalDateTime.now(), // Use current time for event time
                    userNameDto,
                    1L,
                    null
                );

    when(userChatRepository.findByChatIdAndUserId(chatId, userId))
        .thenReturn(Optional.of(new UserChat()));
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
    when(messageRepository.findById(repliedMessageId)).thenReturn(Optional.of(repliedMessage));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(chatRepository.save(any(Chat.class))).thenReturn(chat);
    when(messageMapper.toMessageDto(any(Message.class))).thenReturn(messageDto);

    // Act
    MessageDto result = underTest.saveMessage(request);

    // Assert
    assertEquals(messageDto, result);
    verify(userChatRepository, times(1)).findByChatIdAndUserId(chatId, userId);
    verify(chatRepository, times(1)).findById(chatId);
    verify(messageRepository, times(1)).findById(repliedMessageId);
    verify(userRepository, times(1)).findById(userId);
    verify(chatRepository, times(1)).save(chat);
    verify(messageMapper, times(1)).toMessageDto(any(Message.class));
  }

  @Test
  void saveMessage_shouldThrowUserNotJoinedTheChatException_whenUserNotInChat() {
    // Arrange
    SendMessageRequest request = new SendMessageRequest("Hello", chatId, userId, null);

    when(userChatRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotJoinedTheChatException.class, () -> underTest.saveMessage(request));
  }

  @Test
  void saveMessage_shouldThrowChatNotFoundException_whenChatNotFound() {
    // Arrange
    SendMessageRequest request = new SendMessageRequest("Hello", chatId, userId, null);

    when(userChatRepository.findByChatIdAndUserId(chatId, userId))
        .thenReturn(Optional.of(new UserChat()));
    when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(WebSocketException.class, () -> underTest.saveMessage(request));
  }

  @Test
  void saveMessage_shouldThrowUserNotFoundException_whenUserNotFound() {
    // Arrange
    SendMessageRequest request = new SendMessageRequest("Hello", chatId, userId, null);
    Chat chat = new Chat();

    when(userChatRepository.findByChatIdAndUserId(chatId, userId))
        .thenReturn(Optional.of(new UserChat()));
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(WebSocketException.class, () -> underTest.saveMessage(request));
  }

  @Test
  void saveMessage_shouldThrowMessageNotFoundException_whenRepliedMessageNotFound() {
    // Arrange
    Long repliedMessageId = 2L;
    SendMessageRequest request = new SendMessageRequest("Hello", chatId, userId, repliedMessageId);
    Chat chat = new Chat();

    when(userChatRepository.findByChatIdAndUserId(chatId, userId))
        .thenReturn(Optional.of(new UserChat()));
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
    when(messageRepository.findById(repliedMessageId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(WebSocketException.class, () -> underTest.saveMessage(request));
  }
}
