package online.talkandtravel.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  public static final String JOINED_THE_CHAT = "%s joined the chat";
  public static final String LEFT_THE_CHAT = "%s left the chat";
  public static final int MAX_USERS_IN_PRIVATE_CHAT = 2;
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
    if (chat.getChatType().equals(ChatType.PRIVATE) && chat.getUsers().isEmpty()) {
      chatRepository.delete(chat);
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
    validateUserChatMembership(request);
    removeUserFromChat(request);
    handleUserCountryAssociation(request, chat, author);
  }

  private void validateUserChatMembership(EventRequest request) {
    Optional<UserChat> optionalUserChat =
        userChatRepository.findByChatIdAndUserId(request.chatId(), request.authorId());
    if (optionalUserChat.isEmpty()) {
      throw new UserNotJoinedTheChatException(request.authorId(), request.chatId());
    }
  }

  private void removeUserFromChat(EventRequest request) {
    userChatRepository
        .findByChatIdAndUserId(request.chatId(), request.authorId())
        .ifPresent(userChatRepository::delete);
  }

  private void handleUserCountryAssociation(EventRequest request, Chat chat, User author) {
    if (chat.getCountry() != null) {
      UserCountry userCountry = findUserCountry(chat, author);
      if (isLastChatInCountry(request, userCountry)) {
        removeUserCountryAssociation(userCountry);
      }
    }
  }

  private UserCountry findUserCountry(Chat chat, User author) {
    return userCountryRepository
        .findByCountryNameAndUserId(chat.getCountry().getName(), author.getId())
        .orElseThrow(
            () -> new UserCountryNotFoundException(chat.getCountry().getName(), author.getId()));
  }

  private boolean isLastChatInCountry(EventRequest request, UserCountry userCountry) {
    List<UserChat> userChats =
        userChatRepository.findAllByUserIdAndUserCountryId(request.authorId(), userCountry.getId());
    return userChats.isEmpty();
  }

  private void removeUserCountryAssociation(UserCountry userCountry) {
    userCountryRepository.delete(userCountry);
  }

  private void checkChatIsNotPrivate(EventRequest request, Chat chat) {
    if (chat.getChatType().equals(ChatType.PRIVATE)
        && chat.getUsers().size() >= MAX_USERS_IN_PRIVATE_CHAT) {
      throw new PrivateChatMustContainTwoUsersException(request);
    }
  }

  private void saveConnections(Chat chat, User author) {
    UserCountry userCountry = null;
    if (chat.getCountry() != null) {
      userCountry = findOrCreateUserCountry(chat, author);
    }
    saveUserChat(chat, author, userCountry);
  }

  /**
   * Finds the UserCountry associated with the chat's country and user. If it doesn't exist, creates
   * a new one.
   */
  private UserCountry findOrCreateUserCountry(Chat chat, User author) {
    return userCountryRepository
        .findByCountryNameAndUserId(chat.getCountry().getName(), author.getId())
        .orElseGet(() -> createUserCountry(chat, author));
  }

  /** Creates a new UserCountry entity and associates it with the given user and chat's country. */
  private UserCountry createUserCountry(Chat chat, User author) {
    UserCountry userCountry = UserCountry.builder().country(chat.getCountry()).user(author).build();
    return userCountryRepository.save(userCountry);
  }

  /** Creates and saves a UserChat entity that associates the user with the chat and userCountry. */
  private void saveUserChat(Chat chat, User author, UserCountry userCountry) {
    UserChat userChat = UserChat.builder().chat(chat).user(author).userCountry(userCountry).build();
    if (userCountry != null) {
      // Add the chat to the UserCountry's list of chats
      userCountry.getChats().add(userChat);

      // Save the updated UserCountry
      userCountryRepository.save(userCountry);
    } else {
      userChatRepository.save(userChat);
    }
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
