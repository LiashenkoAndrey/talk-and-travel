package online.talkandtravel.service.impl.unittest;

import static online.talkandtravel.testdata.ChatTestData.ALICE_BOB_PRIVATE_CHAT_ID;
import static online.talkandtravel.testdata.ChatTestData.ARUBA_CHAT_ID;
import static online.talkandtravel.testdata.UserTestData.ALICE_ID;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.exception.user.UserAlreadyJoinTheChatException;
import online.talkandtravel.exception.user.UserCountryNotFoundException;
import online.talkandtravel.model.dto.avatar.AvatarDto;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.dto.event.EventResponse;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.ChatType;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.MessageType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.model.entity.UserCountry;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.repository.UserCountryRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
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
  private static final Long CHAT_ID = 1L;

  @BeforeEach
  void setUp() {
    chat = new Chat();
    chat.setCountry(new Country("Aruba", "ar"));
    chat.setId(CHAT_ID);
    chat.setName("Chat1");
    chat.setChatType(ChatType.GROUP);

    user = getAlice();

    userChat = UserChat.builder().chat(chat).user(user).build();

    userCountry = UserCountry.builder().country(chat.getCountry()).user(user).build();

    message = Message.builder().chat(chat).sender(user).type(MessageType.START_TYPING).build();

    userNameDto = new UserNameDto(1L, "user", new AvatarDto("url", "url"));

    messageDto =
        new MessageDto(
            message.getId(),
            message.getType(),
            "",
            ZonedDateTime.now(ZoneOffset.UTC), // Use current time for event time
            userNameDto,
            chat.getId(),
            null);

    eventResponse = new EventResponse(userNameDto, message.getType(), ZonedDateTime.now(ZoneOffset.UTC));

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
              ZonedDateTime.now(ZoneOffset.UTC) // Use current time for event time
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
      when(userChatRepository.findByChatIdAndUserId(ARUBA_CHAT_ID, ALICE_ID)).thenReturn(Optional.empty());
      when(userCountryRepository.findByCountryNameAndUserId("Country1", ALICE_ID))
          .thenReturn(Optional.of(userCountry));

      when(messageRepository.save(any(Message.class))).thenReturn(message);
      when(messageMapper.toMessageDto(any(Message.class))).thenReturn(messageDto);

      MessageDto result = underTest.joinChat(eventRequest, principal);

      assertEquals(messageDto, result);
      verify(chatRepository, times(1)).findById(1L);
      verify(userChatRepository, times(1)).findByChatIdAndUserId(ARUBA_CHAT_ID, ALICE_ID);
      verify(userCountryRepository, times(1)).findByCountryNameAndUserId("Country1", ALICE_ID);
      verify(userCountryRepository, times(1)).save(any(UserCountry.class));
      verify(messageRepository, times(1)).save(any(Message.class));
      verify(messageMapper, times(1)).toMessageDto(message);
    }

    @Test
    void joinChat_shouldThrowUserAlreadyJoinTheChatException_whenUserAlreadyJoined() {
      when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
      when(userChatRepository.findByChatIdAndUserId(ARUBA_CHAT_ID, ALICE_ID)).thenReturn(Optional.of(userChat));

      assertThrows(UserAlreadyJoinTheChatException.class, () -> underTest.joinChat(eventRequest, principal));
      verify(chatRepository, times(1)).findById(1L);
      verify(userChatRepository, times(1)).findByChatIdAndUserId(ARUBA_CHAT_ID, ALICE_ID);
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

    private static final User alice = getAlice();
    private static final EventRequest leaveChatRequest = new EventRequest(ARUBA_CHAT_ID);
    private Message leaveMessage = Message.builder()
        .chat(chat)
        .sender(alice)
        .type(MessageType.LEAVE)
        .content("Alice left the chat")
        .build();
    @Test
    void leaveChat_shouldReturnDto_whenUserAndChatExist_andUserHasConnections() {
      leaveMessage.setChat(chat);
      chat.setCountry(new Country("Country1", "co"));

      when(chatRepository.findById(ARUBA_CHAT_ID)).thenReturn(Optional.of(chat));
      when(userChatRepository.findByChatIdAndUserId(ARUBA_CHAT_ID, ALICE_ID))
          .thenReturn(Optional.of(new UserChat()));
      when(userCountryRepository.findByCountryNameAndUserId(chat.getCountry().getName(), ALICE_ID))
          .thenReturn(Optional.of(userCountry));
      when(userChatRepository.findAllByUserIdAndUserCountryId(ALICE_ID, userCountry.getId()))
          .thenReturn(Collections.emptyList());
      when(messageRepository.save(leaveMessage)).thenReturn(leaveMessage);
      when(messageMapper.toMessageDto(eq(leaveMessage))).thenReturn(new MessageDto(""));

      MessageDto result = underTest.leaveChat(leaveChatRequest, principal);
      assertNotNull(result);

      verify(chatRepository, times(1)).findById(ARUBA_CHAT_ID);
      verify(userChatRepository, times(2)).findByChatIdAndUserId(ARUBA_CHAT_ID, ALICE_ID);
      verify(userCountryRepository, times(1))
          .findByCountryNameAndUserId(chat.getCountry().getName(), ALICE_ID);
      verify(userChatRepository, times(1)).delete(any(UserChat.class));
      verify(userCountryRepository, times(1)).delete(any(UserCountry.class));
      verify(messageRepository, times(1)).save(leaveMessage);
      verify(messageMapper, times(1)).toMessageDto(any(Message.class));
    }

    @Test
    void leaveChat_shouldDeleteChat_whenLeaveFromPrivateChat() {
      Chat privateChatAliceAndBob = Chat.builder()
          .id(ALICE_BOB_PRIVATE_CHAT_ID)
          .chatType(ChatType.PRIVATE)
          .build();
      EventRequest leaveFromPrivateChatRequest = new EventRequest(ALICE_BOB_PRIVATE_CHAT_ID);
      UserChat aliceUserChat = UserChat.builder().user(alice).build();

      when(chatRepository.findById(ALICE_BOB_PRIVATE_CHAT_ID)).thenReturn(Optional.of(privateChatAliceAndBob));
      when(userChatRepository.findByChatIdAndUserId(ALICE_BOB_PRIVATE_CHAT_ID, ALICE_ID)).thenReturn(
          Optional.of(aliceUserChat));

      underTest.leaveChat(leaveFromPrivateChatRequest, principal);

      verify(userChatRepository).findByChatIdAndUserId(ALICE_BOB_PRIVATE_CHAT_ID, ALICE_ID);
      verify(chatRepository).delete(privateChatAliceAndBob);
      verifyNoInteractions(messageMapper);
      verifyNoInteractions(messageRepository);
    }

    @Test
    void leaveChat_shouldThrowUserNotJoinedTheChatException_whenUserNotInChat() {
      Chat privateChatAliceAndBob = Chat.builder()
          .chatType(ChatType.PRIVATE)
          .build();
      EventRequest leaveFromPrivateChatRequest = new EventRequest(ALICE_BOB_PRIVATE_CHAT_ID);

      when(chatRepository.findById(ALICE_BOB_PRIVATE_CHAT_ID)).thenReturn(Optional.of(privateChatAliceAndBob));
      when(userChatRepository.findByChatIdAndUserId(ALICE_BOB_PRIVATE_CHAT_ID, ALICE_ID)).thenReturn(Optional.empty());

      assertThrows(UserNotJoinedTheChatException.class, () -> underTest.leaveChat(leaveFromPrivateChatRequest, principal));

      verify(userChatRepository).findByChatIdAndUserId(ALICE_BOB_PRIVATE_CHAT_ID, ALICE_ID);

    }

    @Test
    void leaveChat_shouldThrowUserCountryNotFoundException_whenUserCountryNotFound() {
      chat.setCountry(new Country("Country1", "co"));
      when(chatRepository.findById(ARUBA_CHAT_ID)).thenReturn(Optional.of(chat));
      when(userChatRepository.findByChatIdAndUserId(ARUBA_CHAT_ID, ALICE_ID))
          .thenReturn(Optional.of(new UserChat()));
      when(userCountryRepository.findByCountryNameAndUserId(chat.getCountry().getName(), ALICE_ID))
          .thenReturn(Optional.empty());

      assertThrows(UserCountryNotFoundException.class, () -> underTest.leaveChat(eventRequest, principal));
    }
  }

  public void assertEqualsExcludingTime(EventResponse expected, EventResponse actual) {
    assertEquals(expected.user(), actual.user());
    assertEquals(expected.type(), actual.type());
  }
}
