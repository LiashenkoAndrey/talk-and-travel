package online.talkandtravel.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.PrivateChatMustContainTwoUsersException;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.exception.user.UserAlreadyJoinTheChatException;
import online.talkandtravel.exception.user.UserCountryNotFoundException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.dto.event.EventResponse;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.ChatType;
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
import online.talkandtravel.service.EventService;
import online.talkandtravel.util.mapper.MessageMapper;
import online.talkandtravel.util.mapper.UserMapper;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  public static final String JOINED_THE_CHAT = "%s joined the chat";
  public static final String LEFT_THE_CHAT = "%s left the chat";
  private final ChatRepository chatRepository;
  private final UserRepository userRepository;
  private final UserChatRepository userChatRepository;
  private final UserCountryRepository userCountryRepository;
  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;
  private final UserMapper userMapper;

  @Override
  public EventResponse startTyping(EventRequest request) {
    User user = getUser(request);
    throwIfChatNotExists(request);
    return createChatTransientEvent(user, MessageType.START_TYPING);
  }

  @Override
  public EventResponse stopTyping(EventRequest request) {
    User user = getUser(request);
    throwIfChatNotExists(request);
    return createChatTransientEvent(user, MessageType.STOP_TYPING);
  }

  @Transactional
  @Override
  public MessageDto leaveChat(EventRequest request) {
    Chat chat = getChat(request);
    User author = getUser(request);

    removeConnections(request, chat, author);


    Message message =
        Message.builder()
            .content(LEFT_THE_CHAT.formatted(author.getUserName()))
            .chat(chat)
            .sender(author)
            .type(MessageType.LEAVE)
            .build();
    message = messageRepository.save(message);

    return messageMapper.toMessageDto(message);
  }

  @Override
  @Transactional
  public void deleteChatIfEmpty(EventRequest request) {
    Chat chat = getChat(request);
    log.info("chat is empty : {}", chat.getUsers().isEmpty());
    if(chat.getChatType().equals(ChatType.PRIVATE)){
      if (chat.getUsers().isEmpty()){
        chatRepository.delete(chat);
      }
    }
  }

  @Transactional
  @Override
  public MessageDto joinChat(EventRequest request) {
    Chat chat = getChat(request);
    User author = getUser(request);
    checkChatIsNotPrivate(request, chat);
    checkUserAlreadyJoinedChat(request);

    saveConnections(chat, author);

    Message message =
        Message.builder()
            .content(JOINED_THE_CHAT.formatted(author.getUserName()))
            .chat(chat)
            .sender(author)
            .type(MessageType.JOIN)
            .build();
    message = messageRepository.save(message);
    return messageMapper.toMessageDto(message);
  }

  private void removeConnections(EventRequest request, Chat chat, User author) {
    Optional<UserChat> optionalUserChat =
        userChatRepository.findByChatIdAndUserId(request.chatId(), request.authorId());
    if (optionalUserChat.isEmpty()) {
      throw new UserNotJoinedTheChatException(request.authorId(), request.chatId());
    }

    // remove record from userChats
    userChatRepository.delete(optionalUserChat.get());

    if (chat.getCountry() != null) {
      UserCountry userCountry =
          userCountryRepository
              .findByCountryNameAndUserId(chat.getCountry().getName(), author.getId())
              .orElseThrow(
                  () ->
                      new UserCountryNotFoundException(
                          chat.getCountry().getName(), author.getId()));

      // check if there is no records in UserChats with userId and CountryName,
      // if true, remove record from userCountries
      List<UserChat> userChats =
          userChatRepository.findAllByUserIdAndUserCountryId(
              request.authorId(), userCountry.getId());
      if (userChats.isEmpty()) {
        // if this was the last chat in country for user, we remove connection with Country
        userCountryRepository.delete(userCountry);
      }
    }
  }

  private void checkChatIsNotPrivate(EventRequest request, Chat chat) {
    if (chat.getChatType().equals(ChatType.PRIVATE)) {
      throw new PrivateChatMustContainTwoUsersException(request);
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

  private void throwIfChatNotExists(EventRequest request) {
    if (!chatRepository.existsById(request.chatId())) {
      try {
        throw new ChatNotFoundException(request.chatId());
      } catch (ChatNotFoundException e) {
        throw new WebSocketException(e, request.authorId());
      }
    }
  }

  /**
   * creates event that it isn't persisted to a database That is temporary and not intended for
   * persistent storage.
   *
   * @param user User
   * @param messageType type of transient event
   * @return processed event dto
   */
  private EventResponse createChatTransientEvent(User user, MessageType messageType) {
    return new EventResponse(userMapper.toUserNameDto(user), messageType, LocalDateTime.now());
  }
}
