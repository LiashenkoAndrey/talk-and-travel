package online.talkandtravel.service.impl.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.model.dto.avatar.AvatarDto;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.MessageType;
import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.impl.MessageServiceImpl;
import online.talkandtravel.util.mapper.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

  @InjectMocks private MessageServiceImpl underTest;

  @Mock private MessageRepository messageRepository;

  @Mock private ChatRepository chatRepository;

  @Mock private UserChatRepository userChatRepository;

  @Mock private MessageMapper messageMapper;

  private final Long chatId = 1L;
  private final Long userId = 1L;
  private Principal principal;

  @BeforeEach
  void setup() {
    User user = User.builder()
            .id(1L)
            .role(Role.USER)
            .userName("User1")
            .build();
    UserDetails userDetails = new CustomUserDetails(user);
    principal = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
    );
  }

  @Nested
  class SaveMessage {
    @Test
    void replyToMessage_shouldReturnSavedMessage_whenUserJoinedChatAndMessageExists() {
      Long repliedMessageId = 2L;
      String content = "Hello, World!";
      SendMessageRequest request = new SendMessageRequest(content, chatId, repliedMessageId);
      Chat chat = Chat.builder().id(1L).build();
      Message repliedMessage = Message.builder()
          .chat(Chat.builder().id(1L).build())
          .build();
      UserNameDto userNameDto = new UserNameDto(1L, "userName", new AvatarDto("url", "url"));
      MessageDto messageDto =
          new MessageDto(
              1L,
              MessageType.TEXT,
              "",
              ZonedDateTime.now(ZoneOffset.UTC), // Use current time for event time
              userNameDto,
              1L,
              null
          );

      when(userChatRepository.findByChatIdAndUserId(chatId, userId))
          .thenReturn(Optional.of(new UserChat()));
      when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
      when(messageRepository.findById(repliedMessageId)).thenReturn(Optional.of(repliedMessage));
      when(chatRepository.save(any(Chat.class))).thenReturn(chat);
      when(messageMapper.toMessageDto(any(Message.class))).thenReturn(messageDto);

      MessageDto result = underTest.saveMessage(request, principal);

      assertEquals(messageDto, result);
      verify(userChatRepository, times(1)).findByChatIdAndUserId(chatId, userId);
      verify(chatRepository, times(1)).findById(chatId);
      verify(messageRepository, times(1)).findById(repliedMessageId);
      verify(chatRepository, times(1)).save(chat);
      verify(messageMapper, times(1)).toMessageDto(any(Message.class));
    }

    @Test
    void shouldThrowUserNotJoinedTheChatException_whenUserNotInChat() {
      SendMessageRequest request = new SendMessageRequest("Hello", chatId, null);

      when(userChatRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.empty());

      assertThrows(UserNotJoinedTheChatException.class, () -> underTest.saveMessage(request, principal));
    }

    @Test
    void shouldThrowChatNotFoundException_whenChatNotFound() {
      SendMessageRequest request = new SendMessageRequest("Hello", chatId, null);

      when(userChatRepository.findByChatIdAndUserId(chatId, userId))
          .thenReturn(Optional.of(new UserChat()));
      when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

      assertThrows(WebSocketException.class, () -> underTest.saveMessage(request, principal));
    }

    @Test
    void shouldThrowMessageNotFoundException_whenRepliedMessageNotFound() {
      Long repliedMessageId = 2L;
      SendMessageRequest request = new SendMessageRequest("Hello", chatId, repliedMessageId);
      Chat chat = new Chat();

      when(userChatRepository.findByChatIdAndUserId(chatId, userId))
          .thenReturn(Optional.of(new UserChat()));
      when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
      when(messageRepository.findById(repliedMessageId)).thenReturn(Optional.empty());

      assertThrows(WebSocketException.class, () -> underTest.saveMessage(request, principal));
    }
  }

}
