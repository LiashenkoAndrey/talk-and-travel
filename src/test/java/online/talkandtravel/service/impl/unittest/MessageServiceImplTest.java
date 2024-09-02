package online.talkandtravel.service.impl.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.enums.MessageType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.service.AuthenticationService;
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

  @Mock private UserChatRepository userChatRepository;

  @Mock private MessageMapper messageMapper;

  @Mock private AuthenticationService authenticationService;

  private final Long chatId = 1L;
  private final Long userId = 1L;

  private static final User authUser = User.builder().id(1L).build();


  @Test
  void saveMessage_shouldReturnMessageDtoBasic_whenUserJoinedChatAndMessageExists() {

    Long repliedMessageId = 2L;
    String content = "Hello, World!";
    SendMessageRequest request = new SendMessageRequest(content, chatId, repliedMessageId);
    Chat chat = new Chat();
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

    when(authenticationService.getAuthenticatedUser()).thenReturn(authUser);
    when(userChatRepository.findByChatIdAndUserId(chatId, userId))
        .thenReturn(Optional.of(new UserChat()));
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
    when(messageRepository.findById(repliedMessageId)).thenReturn(Optional.of(repliedMessage));
    when(chatRepository.save(any(Chat.class))).thenReturn(chat);
    when(messageMapper.toMessageDto(any(Message.class))).thenReturn(messageDto);

    MessageDto result = underTest.saveMessage(request);

    assertEquals(messageDto, result);
    verify(userChatRepository, times(1)).findByChatIdAndUserId(chatId, userId);
    verify(chatRepository, times(1)).findById(chatId);
    verify(messageRepository, times(1)).findById(repliedMessageId);
    verify(chatRepository, times(1)).save(chat);
    verify(messageMapper, times(1)).toMessageDto(any(Message.class));
  }

  @Test
  void saveMessage_shouldThrowUserNotJoinedTheChatException_whenUserNotInChat() {
    SendMessageRequest request = new SendMessageRequest("Hello", chatId, null);

    when(authenticationService.getAuthenticatedUser()).thenReturn(authUser);
    when(userChatRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.empty());

    assertThrows(UserNotJoinedTheChatException.class, () -> underTest.saveMessage(request));
  }

  @Test
  void saveMessage_shouldThrowChatNotFoundException_whenChatNotFound() {
    // Arrange
    SendMessageRequest request = new SendMessageRequest("Hello", chatId, null);

    when(authenticationService.getAuthenticatedUser()).thenReturn(authUser);
    when(userChatRepository.findByChatIdAndUserId(chatId, userId))
        .thenReturn(Optional.of(new UserChat()));
    when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(WebSocketException.class, () -> underTest.saveMessage(request));
  }

  @Test
  void saveMessage_shouldThrowMessageNotFoundException_whenRepliedMessageNotFound() {
    Long repliedMessageId = 2L;
    SendMessageRequest request = new SendMessageRequest("Hello", chatId, repliedMessageId);
    Chat chat = new Chat();

    when(authenticationService.getAuthenticatedUser()).thenReturn(authUser);
    when(userChatRepository.findByChatIdAndUserId(chatId, userId))
        .thenReturn(Optional.of(new UserChat()));
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
    when(messageRepository.findById(repliedMessageId)).thenReturn(Optional.empty());

    assertThrows(WebSocketException.class, () -> underTest.saveMessage(request));
  }
}
