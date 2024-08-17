package online.talkandtravel.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.exception.user.UserAlreadyJoinTheChatException;
import online.talkandtravel.exception.user.UserCountryNotFoundException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.entity.Chat;
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
import online.talkandtravel.service.EventService;
import online.talkandtravel.util.mapper.EventMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link EventService} for managing chat-related events.
 *
 * <p>This service provides methods for handling various events in a chat, including:
 *
 * <ul>
 *   <li>{@link #startTyping(EventRequest)} - Logs an event when a user starts typing in a chat.
 *   <li>{@link #stopTyping(EventRequest)} - Logs an event when a user stops typing in a chat.
 *   <li>{@link #leaveChat(EventRequest)} - Manages the user's departure from a chat and records the
 *       event.
 *   <li>{@link #joinChat(EventRequest)} - Handles the user's entry into a chat and records the
 *       event.
 * </ul>
 *
 * <p>The service also includes internal methods to manage user-chat and user-country relationships:
 *
 * <ul>
 *   <li>{@link #removeConnections(EventRequest, Chat, User)} - Removes user connections and updates
 *       records when a user leaves a chat.
 *   <li>{@link #saveConnections(Chat, User)} - Creates and saves connections between a user and a
 *       chat, and updates user-country relationships.
 *   <li>{@link #checkUserAlreadyJoinedChat(EventRequest)} - Checks if a user is already part of the
 *       chat to prevent duplicate entries.
 *   <li>{@link #getUser(EventRequest)} - Retrieves a user entity by ID, or throws a {@link
 *       UserNotFoundException} if not found.
 *   <li>{@link #getChat(EventRequest)} - Retrieves a chat entity by ID, or throws a {@link
 *       ChatNotFoundException} if not found.
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
  private final ChatRepository chatRepository;
  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final EventMapper eventMapper;
  private final UserChatRepository userChatRepository;
  private final UserCountryRepository userCountryRepository;

  @Override
  public EventDtoBasic startTyping(EventRequest request) {
    Chat chat = getChat(request);
    User author = getUser(request);

    Event event = Event.builder().chat(chat).user(author).eventType(EventType.START_TYPING).build();
    event = eventRepository.save(event);
    return eventMapper.toEventDtoBasic(event);
  }

  @Override
  public EventDtoBasic stopTyping(EventRequest request) {
    Chat chat = getChat(request);
    User author = getUser(request);

    Event event = Event.builder().chat(chat).user(author).eventType(EventType.STOP_TYPING).build();
    event = eventRepository.save(event);
    return eventMapper.toEventDtoBasic(event);
  }

  @Transactional
  @Override
  public EventDtoBasic leaveChat(EventRequest request) {
    Chat chat = getChat(request);
    User author = getUser(request);

    removeConnections(request, chat, author);

    Event event = Event.builder().chat(chat).user(author).eventType(EventType.LEAVE).build();
    event = eventRepository.save(event);
    return eventMapper.toEventDtoBasic(event);
  }

  @Transactional
  @Override
  public EventDtoBasic joinChat(EventRequest request) {
    Chat chat = getChat(request);
    User author = getUser(request);
    checkUserAlreadyJoinedChat(request);

    saveConnections(chat, author);

    Event event = Event.builder().chat(chat).user(author).eventType(EventType.JOIN).build();
    event = eventRepository.save(event);
    return eventMapper.toEventDtoBasic(event);
  }

  private void removeConnections(EventRequest request, Chat chat, User author) {
    Optional<UserChat> optionalUserChat =
        userChatRepository.findByChatIdAndUserId(request.chatId(), request.authorId());
    if (optionalUserChat.isEmpty()) {
      throw new UserNotJoinedTheChatException(request.authorId(), request.chatId());
    }

    UserCountry userCountry =
        userCountryRepository
            .findByCountryNameAndUserId(chat.getCountry().getName(), author.getId())
            .orElseThrow(
                () ->
                    new UserCountryNotFoundException(chat.getCountry().getName(), author.getId()));

    // remove record from userChats
    userChatRepository.delete(optionalUserChat.get());

    // check if there is no records in UserChats with userId and CountryName,
    // if true, remove record from userCountries
    List<UserChat> userChats =
        userChatRepository.findAllByUserIdAndUserCountryId(request.authorId(), userCountry.getId());
    if (userChats.isEmpty()) {
      // if this was the last chat in country for user, we remove connection with Country
      userCountryRepository.delete(userCountry);
    }
  }

  private void saveConnections(Chat chat, User author) {
    Optional<UserCountry> userCountryOptional =
        userCountryRepository.findByCountryNameAndUserId(
            chat.getCountry().getName(), author.getId());

    UserCountry userCountry =
        userCountryOptional.orElseGet(
            () -> UserCountry.builder().country(chat.getCountry()).user(author).build());

    UserChat userChat = UserChat.builder().chat(chat).user(author).userCountry(userCountry).build();

    userCountry.getChats().add(userChat);

    // save connection user with chat
    userCountryRepository.save(userCountry);
  }

  private void checkUserAlreadyJoinedChat(EventRequest request) {
    Optional<UserChat> userChats =
        userChatRepository.findByChatIdAndUserId(request.chatId(), request.authorId());
    if (userChats.isPresent()) {
      throw new UserAlreadyJoinTheChatException(request.authorId(), request.chatId());
    }
  }

  private User getUser(EventRequest request) {
    try {
      return userRepository
          .findById(request.authorId())
          .orElseThrow(() -> new UserNotFoundException(request.authorId()));
    } catch (UserNotFoundException e) {
      throw new WebSocketException(e, request.authorId());
    }
  }

  private Chat getChat(EventRequest request) {
    try {
      return chatRepository
          .findById(request.chatId())
          .orElseThrow(() -> new ChatNotFoundException(request.chatId()));
    } catch (ChatNotFoundException e) {
      throw new WebSocketException(e, request.authorId());
    }
  }
}
