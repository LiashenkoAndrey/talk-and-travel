package online.talkandtravel.service.impl.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.exception.user.UserAlreadyJoinTheChatException;
import online.talkandtravel.exception.user.UserCountryNotFoundException;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.dto.event.EventResponse;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.ChatType;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.MessageType;
import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.model.entity.UserCountry;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.repository.UserCountryRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.impl.EventServiceImpl;
import online.talkandtravel.util.mapper.MessageMapper;
import online.talkandtravel.util.mapper.UserMapper;
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
class EventServiceImplTest {

  @Mock private ChatRepository chatRepository;
  @Mock private MessageRepository messageRepository;
  @Mock private UserRepository userRepository;
  @Mock private UserMapper userMapper;
  @Mock private MessageMapper messageMapper;
  @Mock private UserChatRepository userChatRepository;
  @Mock private UserCountryRepository userCountryRepository;
  @Mock private AuthenticationService authenticationService;

  @InjectMocks private EventServiceImpl underTest;

  private Chat chat;
  private User user;
  private Message message;
  private MessageDto messageDto;
  private UserNameDto userNameDto;
  private EventResponse eventResponse;
  private EventRequest eventRequest;
  private UserChat userChat;
  private UserCountry userCountry;

  private Principal principal;
  private static final Long CHAT_ID = 1L, USER_ID = 1L;

  @BeforeEach
  void setUp() {
    chat = new Chat();
    chat.setId(CHAT_ID);
    chat.setName("Chat1");
    chat.setChatType(ChatType.GROUP);

    user = User.builder()
        .id(USER_ID)
        .role(Role.USER)
        .userName("User1")
        .build();

    userChat = UserChat.builder().chat(chat).user(user).build();

    userCountry = UserCountry.builder().country(chat.getCountry()).user(user).build();

    message = Message.builder().chat(chat).sender(user).type(MessageType.START_TYPING).build();

    userNameDto = new UserNameDto(1L, "user");

    messageDto =
        new MessageDto(
            message.getId(),
            message.getType(),
            "",
            LocalDateTime.now(), // Use current time for event time
            userNameDto,
            chat.getId(),
            null);

    eventResponse = new EventResponse(userNameDto, message.getType(), LocalDateTime.now());

    eventRequest = new EventRequest(CHAT_ID);

    UserDetails userDetails = new CustomUserDetails(user);
    principal = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );
  }

  @Nested
  class StartTyping {

    private final Long chatId = 1L;

    @Test
    void startTyping_shouldReturnEventDtoBasic_whenChatAndUserExist() {
      when(chatRepository.existsById(chatId)).thenReturn(true);
      when(userMapper.toUserNameDto(user)).thenReturn(userNameDto);

      EventResponse result = underTest.startTyping(eventRequest, principal);

      assertEqualsExcludingTime(eventResponse, result);
      verify(chatRepository, times(1)).existsById(chatId);
    }

    @Test
    void startTyping_shouldThrowChatNotFoundException_whenChatDoesNotExist() {
      when(chatRepository.existsById(chatId)).thenReturn(false);

      assertThrows(WebSocketException.class, () -> underTest.startTyping(eventRequest, principal));
      verify(chatRepository, times(1)).existsById(anyLong());
      verify(messageRepository, never()).save(any(Message.class));
      verify(messageMapper, never()).toMessageDto(any(Message.class));
    }

  }

  @Nested
  class StopTyping {

    private final Long chatId = 1L;

    @Test
    void stopTyping_shouldReturnEventResponse_whenChatAndUserExist() {
      when(chatRepository.existsById(chatId)).thenReturn(true);
      when(userMapper.toUserNameDto(user)).thenReturn(userNameDto);
      EventResponse expected =
          new EventResponse(
              userNameDto,
              MessageType.STOP_TYPING,
              LocalDateTime.now() // Use current time for event time
              );

      EventResponse result = underTest.stopTyping(eventRequest, principal);

      assertEqualsExcludingTime(expected, result);
      verify(chatRepository, times(1)).existsById(chatId);
    }

    @Test
    void stopTyping_shouldThrowChatNotFoundException_whenChatDoesNotExist() {
      when(chatRepository.existsById(chatId)).thenReturn(false);

      assertThrows(WebSocketException.class, () -> underTest.stopTyping(eventRequest, principal));
      verify(chatRepository, times(1)).existsById(1L);
      verify(messageRepository, never()).save(any(Message.class));
      verify(messageMapper, never()).toMessageDto(any(Message.class));
    }

  }

  @Nested
  class JoinChat {
    @Test
    void joinChat_shouldReturnEventDtoBasic_whenChatAndUserExistAndUserNotJoined() {

      chat.setCountry(new Country("Country1", "co"));
      when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
      when(userChatRepository.findByChatIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
      when(userCountryRepository.findByCountryNameAndUserId("Country1", 1L))
          .thenReturn(Optional.of(userCountry));

      when(messageRepository.save(any(Message.class))).thenReturn(message);
      when(messageMapper.toMessageDto(any(Message.class))).thenReturn(messageDto);

      MessageDto result = underTest.joinChat(eventRequest, principal);

      assertEquals(messageDto, result);
      verify(chatRepository, times(1)).findById(1L);
      verify(userChatRepository, times(1)).findByChatIdAndUserId(1L, 1L);
      verify(userCountryRepository, times(1)).findByCountryNameAndUserId("Country1", 1L);
      verify(userCountryRepository, times(1)).save(any(UserCountry.class));
      verify(messageRepository, times(1)).save(any(Message.class));
      verify(messageMapper, times(1)).toMessageDto(message);
    }

    @Test
    void joinChat_shouldThrowUserAlreadyJoinTheChatException_whenUserAlreadyJoined() {
      when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
      when(userChatRepository.findByChatIdAndUserId(1L, 1L)).thenReturn(Optional.of(userChat));

      assertThrows(UserAlreadyJoinTheChatException.class, () -> underTest.joinChat(eventRequest, principal));
      verify(chatRepository, times(1)).findById(1L);
      verify(userChatRepository, times(1)).findByChatIdAndUserId(1L, 1L);
      verify(userCountryRepository, never()).findByCountryNameAndUserId(anyString(), anyLong());
      verify(userChatRepository, never()).save(any(UserChat.class));
      verify(userCountryRepository, never()).save(any(UserCountry.class));
      verify(messageRepository, never()).save(any(Message.class));
      verify(messageMapper, never()).toMessageDto(any(Message.class));
    }

    @Test
    void joinChat_shouldThrowChatNotFoundException_whenChatDoesNotExist() {
      when(chatRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(WebSocketException.class, () -> underTest.joinChat(eventRequest, principal));
      verify(chatRepository, times(1)).findById(1L);
      verify(userRepository, never()).findById(anyLong());
      verify(userChatRepository, never()).findByChatIdAndUserId(anyLong(), anyLong());
      verify(userCountryRepository, never()).findByCountryNameAndUserId(anyString(), anyLong());
      verify(userChatRepository, never()).save(any(UserChat.class));
      verify(userCountryRepository, never()).save(any(UserCountry.class));
      verify(messageRepository, never()).save(any(Message.class));
      verify(messageMapper, never()).toMessageDto(any(Message.class));
    }

    @Test
    void joinChat_shouldThrowPrivateChatException_whenChatIsPrivateAndFull() {
      chat.setChatType(ChatType.PRIVATE);
      User companion = new User();
      companion.setId(2L);
      companion.setUserName("User2");
      chat.getUsers().add(companion);
      chat.getUsers().add(user);
      when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

      assertThrows(WebSocketException.class, () -> underTest.joinChat(eventRequest, principal));
      verify(chatRepository, times(1)).findById(1L);
      verify(userChatRepository, never()).findByChatIdAndUserId(anyLong(), anyLong());
      verify(userCountryRepository, never()).findByCountryNameAndUserId(anyString(), anyLong());
      verify(userChatRepository, never()).save(any(UserChat.class));
      verify(userCountryRepository, never()).save(any(UserCountry.class));
      verify(messageRepository, never()).save(any(Message.class));
      verify(messageMapper, never()).toMessageDto(any(Message.class));
    }
  }

  @Nested
  class LeaveChat {
    @Test
    void leaveChat_shouldReturnEventDtoBasic_whenUserAndChatExist_andUserHasConnections() {
      // Arrange
      chat.setCountry(new Country("Country1", "co"));
      MessageDto eventDtoBasic =
          new MessageDto(
              null, MessageType.START_TYPING, "", LocalDateTime.now(), userNameDto, CHAT_ID, null);

      when(chatRepository.findById(CHAT_ID)).thenReturn(Optional.of(chat));
      when(userChatRepository.findByChatIdAndUserId(CHAT_ID, USER_ID))
          .thenReturn(Optional.of(new UserChat()));
      when(userCountryRepository.findByCountryNameAndUserId(chat.getCountry().getName(), USER_ID))
          .thenReturn(Optional.of(userCountry));
      when(userChatRepository.findAllByUserIdAndUserCountryId(USER_ID, userCountry.getId()))
          .thenReturn(Collections.emptyList());
      when(messageRepository.save(any(Message.class))).thenReturn(message);
      when(messageMapper.toMessageDto(message)).thenReturn(messageDto);

      // Act
      MessageDto result = underTest.leaveChat(eventRequest, principal);

      // Assert
      assertEqualsExcludingTime(eventDtoBasic, result);
      verify(chatRepository, times(1)).findById(CHAT_ID);
      verify(userChatRepository, times(2)).findByChatIdAndUserId(CHAT_ID, USER_ID);
      verify(userCountryRepository, times(1))
          .findByCountryNameAndUserId(chat.getCountry().getName(), USER_ID);
      verify(userChatRepository, times(1)).delete(any(UserChat.class));
      verify(userCountryRepository, times(1)).delete(any(UserCountry.class));
      verify(messageRepository, times(1)).save(any(Message.class));
      verify(messageMapper, times(1)).toMessageDto(message);
    }

    @Test
    void leaveChat_shouldThrowUserNotJoinedTheChatException_whenUserNotInChat() {
      // Arrange
      when(chatRepository.findById(CHAT_ID)).thenReturn(Optional.of(new Chat()));
      when(userChatRepository.findByChatIdAndUserId(CHAT_ID, USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertThrows(UserNotJoinedTheChatException.class, () -> underTest.leaveChat(eventRequest, principal));
    }

    @Test
    void leaveChat_shouldThrowUserCountryNotFoundException_whenUserCountryNotFound() {
      // Arrange
      chat.setCountry(new Country("Country1", "co"));
      when(chatRepository.findById(CHAT_ID)).thenReturn(Optional.of(chat));
      when(userChatRepository.findByChatIdAndUserId(CHAT_ID, USER_ID))
          .thenReturn(Optional.of(new UserChat()));
      when(userCountryRepository.findByCountryNameAndUserId(chat.getCountry().getName(), USER_ID))
          .thenReturn(Optional.empty());

      // Act & Assert
      assertThrows(UserCountryNotFoundException.class, () -> underTest.leaveChat(eventRequest, principal));
    }
  }

  public void assertEqualsExcludingTime(MessageDto expected, MessageDto actual) {
    assertEquals(expected.type(), actual.type());
    assertEquals(expected.content(), actual.content());
    assertEquals(expected.repliedMessageId(), actual.repliedMessageId());
    assertEquals(expected.id(), actual.id());
    assertEquals(expected.chatId(), actual.chatId());
    assertEquals(expected.user(), actual.user());
  }

  public void assertEqualsExcludingTime(EventResponse expected, EventResponse actual) {
    assertEquals(expected.user(), actual.user());
    assertEquals(expected.type(), actual.type());
  }
}
