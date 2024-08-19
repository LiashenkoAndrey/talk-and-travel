package online.talkandtravel.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.exception.user.UserAlreadyJoinTheChatException;
import online.talkandtravel.exception.user.UserCountryNotFoundException;
import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.dto.event.EventResponse;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.Event;
import online.talkandtravel.model.entity.EventType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.model.entity.UserCountry;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.EventRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.repository.UserCountryRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.util.mapper.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

  @Mock
  private ChatRepository chatRepository;
  @Mock
  private EventRepository eventRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private EventMapper eventMapper;
  @Mock
  private UserChatRepository userChatRepository;
  @Mock
  private UserCountryRepository userCountryRepository;

  @InjectMocks
  private EventServiceImpl underTest;

  private Chat chat;
  private User user;
  private Event event;
  private EventDtoBasic eventDtoBasic;
  private EventResponse eventResponse;
  private EventRequest eventRequest;
  private UserChat userChat;
  private UserCountry userCountry;
  private final Long chatId = 1L, userId = 1L;

  @BeforeEach
  void setUp() {
    chat = new Chat();
    Long chatId = 1L;
    chat.setId(chatId);
    chat.setName("Chat1");

    user = new User();
    Long userId = 1L;
    user.setId(userId);
    user.setUserName("User1");

    userChat = UserChat.builder().chat(chat).user(user).build();

    userCountry = UserCountry.builder().country(chat.getCountry()).user(user).build();

    event = Event.builder().chat(chat).user(user).eventType(EventType.START_TYPING).build();

    eventDtoBasic =
        new EventDtoBasic(
            event.getId(),
            user.getId(),
            chat.getId(),
            event.getEventType(),
            LocalDateTime.now() // Use current time for event time
        );

    eventResponse = new EventResponse(userId, event.getEventType(), LocalDateTime.now());

    eventRequest = new EventRequest(userId, chatId);
  }

  @Nested
  class StartTyping {

    private final Long chatId = 1L, userId = 1L;

    @Test
    void startTyping_shouldReturnEventDtoBasic_whenChatAndUserExist() {
      when(chatRepository.existsById(chatId)).thenReturn(true);
      when(userRepository.existsById(userId)).thenReturn(true);

      EventResponse result = underTest.startTyping(eventRequest);

      assertEqualsExcludingTime(eventResponse, result);
      verify(chatRepository, times(1)).existsById(chatId);
      verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void startTyping_shouldThrowChatNotFoundException_whenChatDoesNotExist() {
      when(chatRepository.existsById(chatId)).thenReturn(false);

      assertThrows(WebSocketException.class, () -> underTest.startTyping(eventRequest));
      verify(chatRepository, times(1)).existsById(1L);
      verify(userRepository, never()).findById(anyLong());
      verify(eventRepository, never()).save(any(Event.class));
      verify(eventMapper, never()).toEventDtoBasic(any(Event.class));
    }

    @Test
    void startTyping_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
      when(chatRepository.existsById(chatId)).thenReturn(true);
      when(userRepository.existsById(userId)).thenReturn(false);

      assertThrows(WebSocketException.class, () -> underTest.startTyping(eventRequest));
      verify(chatRepository, times(1)).existsById(1L);
      verify(userRepository, times(1)).existsById(1L);
      verify(eventRepository, never()).save(any(Event.class));
      verify(eventMapper, never()).toEventDtoBasic(any(Event.class));
    }
  }

  @Nested
  class StopTyping {

    private final Long chatId = 1L, userId = 1L;

    @Test
    void stopTyping_shouldReturnEventDtoBasic_whenChatAndUserExist() {
      when(chatRepository.existsById(chatId)).thenReturn(true);
      when(userRepository.existsById(userId)).thenReturn(true);
      EventResponse expected =
          new EventResponse(
              user.getId(),
              EventType.STOP_TYPING,
              LocalDateTime.now() // Use current time for event time
          );

      EventResponse result = underTest.stopTyping(eventRequest);

      assertEqualsExcludingTime(expected, result);
      verify(chatRepository, times(1)).existsById(chatId);
      verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void stopTyping_shouldThrowChatNotFoundException_whenChatDoesNotExist() {
      when(chatRepository.existsById(chatId)).thenReturn(false);

      assertThrows(WebSocketException.class, () -> underTest.stopTyping(eventRequest));
      verify(chatRepository, times(1)).existsById(1L);
      verify(userRepository, never()).findById(anyLong());
      verify(eventRepository, never()).save(any(Event.class));
      verify(eventMapper, never()).toEventDtoBasic(any(Event.class));
    }

    @Test
    void stopTyping_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
      when(chatRepository.existsById(chatId)).thenReturn(true);
      when(userRepository.existsById(userId)).thenReturn(false);

      assertThrows(WebSocketException.class, () -> underTest.stopTyping(eventRequest));
      verify(chatRepository, times(1)).existsById(1L);
      verify(userRepository, times(1)).existsById(1L);
      verify(eventRepository, never()).save(any(Event.class));
      verify(eventMapper, never()).toEventDtoBasic(any(Event.class));
    }
  }

  @Test
  void joinChat_shouldReturnEventDtoBasic_whenChatAndUserExist_andUserNotJoined() {

    chat.setCountry(new Country("Country1", "co"));
    when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userChatRepository.findByChatIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
    when(userCountryRepository.findByCountryNameAndUserId("Country1", 1L))
        .thenReturn(Optional.of(userCountry));

    when(eventRepository.save(any(Event.class))).thenReturn(event);
    when(eventMapper.toEventDtoBasic(any(Event.class))).thenReturn(eventDtoBasic);

    EventDtoBasic result = underTest.joinChat(eventRequest);

    assertEquals(eventDtoBasic, result);
    verify(chatRepository, times(1)).findById(1L);
    verify(userRepository, times(1)).findById(1L);
    verify(userChatRepository, times(1)).findByChatIdAndUserId(1L, 1L);
    verify(userCountryRepository, times(1)).findByCountryNameAndUserId("Country1", 1L);
    verify(userCountryRepository, times(1)).save(any(UserCountry.class));
    verify(eventRepository, times(1)).save(any(Event.class));
    verify(eventMapper, times(1)).toEventDtoBasic(event);
  }

  @Test
  void joinChat_shouldThrowUserAlreadyJoinTheChatException_whenUserAlreadyJoined() {
    when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userChatRepository.findByChatIdAndUserId(1L, 1L)).thenReturn(Optional.of(userChat));

    assertThrows(UserAlreadyJoinTheChatException.class, () -> underTest.joinChat(eventRequest));
    verify(chatRepository, times(1)).findById(1L);
    verify(userRepository, times(1)).findById(1L);
    verify(userChatRepository, times(1)).findByChatIdAndUserId(1L, 1L);
    verify(userCountryRepository, never()).findByCountryNameAndUserId(anyString(), anyLong());
    verify(userChatRepository, never()).save(any(UserChat.class));
    verify(userCountryRepository, never()).save(any(UserCountry.class));
    verify(eventRepository, never()).save(any(Event.class));
    verify(eventMapper, never()).toEventDtoBasic(any(Event.class));
  }

  @Test
  void joinChat_shouldThrowChatNotFoundException_whenChatDoesNotExist() {
    when(chatRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(WebSocketException.class, () -> underTest.joinChat(eventRequest));
    verify(chatRepository, times(1)).findById(1L);
    verify(userRepository, never()).findById(anyLong());
    verify(userChatRepository, never()).findByChatIdAndUserId(anyLong(), anyLong());
    verify(userCountryRepository, never()).findByCountryNameAndUserId(anyString(), anyLong());
    verify(userChatRepository, never()).save(any(UserChat.class));
    verify(userCountryRepository, never()).save(any(UserCountry.class));
    verify(eventRepository, never()).save(any(Event.class));
    verify(eventMapper, never()).toEventDtoBasic(any(Event.class));
  }

  @Test
  void joinChat_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
    when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(WebSocketException.class, () -> underTest.joinChat(eventRequest));
    verify(chatRepository, times(1)).findById(1L);
    verify(userRepository, times(1)).findById(1L);
    verify(userChatRepository, never()).findByChatIdAndUserId(anyLong(), anyLong());
    verify(userCountryRepository, never()).findByCountryNameAndUserId(anyString(), anyLong());
    verify(userChatRepository, never()).save(any(UserChat.class));
    verify(userCountryRepository, never()).save(any(UserCountry.class));
    verify(eventRepository, never()).save(any(Event.class));
    verify(eventMapper, never()).toEventDtoBasic(any(Event.class));
  }

  @Test
  void leaveChat_shouldReturnEventDtoBasic_whenUserAndChatExist_andUserHasConnections() {
    // Arrange
    chat.setCountry(new Country("Country1", "co"));
    EventDtoBasic eventDtoBasic =
        new EventDtoBasic(1L, userId, chatId, EventType.LEAVE, LocalDateTime.now());

    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userChatRepository.findByChatIdAndUserId(chatId, userId))
        .thenReturn(Optional.of(new UserChat()));
    when(userCountryRepository.findByCountryNameAndUserId(chat.getCountry().getName(), userId))
        .thenReturn(Optional.of(userCountry));
    when(userChatRepository.findAllByUserIdAndUserCountryId(userId, userCountry.getId()))
        .thenReturn(Collections.emptyList());
    when(eventRepository.save(any(Event.class))).thenReturn(event);
    when(eventMapper.toEventDtoBasic(event)).thenReturn(eventDtoBasic);

    // Act
    EventDtoBasic result = underTest.leaveChat(eventRequest);

    // Assert
    assertEquals(eventDtoBasic, result);
    verify(chatRepository, times(1)).findById(chatId);
    verify(userRepository, times(1)).findById(userId);
    verify(userChatRepository, times(1)).findByChatIdAndUserId(chatId, userId);
    verify(userCountryRepository, times(1))
        .findByCountryNameAndUserId(chat.getCountry().getName(), userId);
    verify(userChatRepository, times(1)).delete(any(UserChat.class));
    verify(userCountryRepository, times(1)).delete(any(UserCountry.class));
    verify(eventRepository, times(1)).save(any(Event.class));
    verify(eventMapper, times(1)).toEventDtoBasic(event);
  }

  @Test
  void leaveChat_shouldThrowUserNotJoinedTheChatException_whenUserNotInChat() {
    // Arrange
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(new Chat()));
    when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
    when(userChatRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotJoinedTheChatException.class, () -> underTest.leaveChat(eventRequest));
  }

  @Test
  void leaveChat_shouldThrowUserCountryNotFoundException_whenUserCountryNotFound() {
    // Arrange
    chat.setCountry(new Country("Country1", "co"));
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userChatRepository.findByChatIdAndUserId(chatId, userId))
        .thenReturn(Optional.of(new UserChat()));
    when(userCountryRepository.findByCountryNameAndUserId(chat.getCountry().getName(), userId))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserCountryNotFoundException.class, () -> underTest.leaveChat(eventRequest));
  }


  public void assertEqualsExcludingTime(EventResponse expected, EventResponse actual) {
    assertEquals(expected.authorId(), actual.authorId());
    assertEquals(expected.eventType(), actual.eventType());
  }
}
