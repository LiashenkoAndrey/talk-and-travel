package online.talkandtravel.service.impl;

import java.security.Principal;
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
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.EventService;
import online.talkandtravel.util.mapper.MessageMapper;
import online.talkandtravel.util.mapper.UserMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link EventService} for managing chat-related events.
 *
 * <p>This service provides methods for handling various events in a chat, including:
 *
 * <ul>
 *   <li>{@link #startTyping(EventRequest, Principal)} - Logs an event when a user starts typing in a chat.
 *   <li>{@link #stopTyping(EventRequest, Principal)} - Logs an event when a user stops typing in a chat.
 *   <li>{@link #leaveChat(EventRequest, Principal)} - Manages the user's departure from a chat and records the
 *       event.
 *   <li>{@link #joinChat(EventRequest, Principal)} - Handles the user's entry into a chat and records the
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
 *   <li>{@link #checkUserAlreadyJoinedChat(EventRequest, Long)} - Checks if a user is already part of the
 *       chat to prevent duplicate entries.
 *   <li>{@link #getChat(EventRequest, Long)} - Retrieves a chat entity by ID, or throws a {@link
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
  private final UserChatRepository userChatRepository;
  private final UserCountryRepository userCountryRepository;
  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;
  private final UserMapper userMapper;

  @Override
  public EventResponse startTyping(EventRequest request, Principal principal) {
    User user = getUser(principal);
    throwIfChatNotExists(request, user.getId());
    return createChatTransientEvent(user, MessageType.START_TYPING);
  }

  @Override
  public EventResponse stopTyping(EventRequest request, Principal principal) {
    User user = getUser(principal);
    throwIfChatNotExists(request, user.getId());
    return createChatTransientEvent(user, MessageType.STOP_TYPING);
  }

  @Transactional
  @Override
  public MessageDto leaveChat(EventRequest request, Principal principal) {
    User author = getUser(principal);
    Chat chat = getChat(request, author.getId());

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
  public void deleteChatIfEmpty(EventRequest request, Principal principal) {
    User user = getUser(principal);
    Chat chat = getChat(request, user.getId());
    if (chat.getChatType().equals(ChatType.PRIVATE) && chat.getUsers().isEmpty()) {
      chatRepository.delete(chat);
    }
  }

  @Transactional
  @Override
  public MessageDto joinChat(EventRequest request, Principal principal) {
    User author = getUser(principal);
    Chat chat = getChat(request, author.getId());
    checkChatIsNotPrivate(request, chat, author.getId());
    checkUserAlreadyJoinedChat(request, author.getId());

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

  private User getUser(Principal principal) {
    CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    return customUserDetails.getUser();
  }

  private void removeConnections(EventRequest request, Chat chat, User author) {
    validateUserChatMembership(request, author.getId());
    removeUserFromChat(request, author.getId());
    handleUserCountryAssociation(chat, author);
  }

  private void validateUserChatMembership(EventRequest request, Long authorId) {
    Optional<UserChat> optionalUserChat =
        userChatRepository.findByChatIdAndUserId(request.chatId(), authorId);
    if (optionalUserChat.isEmpty()) {
      throw new UserNotJoinedTheChatException(authorId, request.chatId());
    }
  }

  private void removeUserFromChat(EventRequest request, Long authorId) {
    userChatRepository
        .findByChatIdAndUserId(request.chatId(), authorId)
        .ifPresent(userChatRepository::delete);
  }

  private void handleUserCountryAssociation(Chat chat, User author) {
    if (chat.getCountry() != null) {
      UserCountry userCountry = findUserCountry(chat, author);
      if (isLastChatInCountry(author.getId(), userCountry.getId())) {
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

  private boolean isLastChatInCountry(Long authorId, Long countryId) {
    List<UserChat> userChats =
        userChatRepository.findAllByUserIdAndUserCountryId(authorId, countryId);
    return userChats.isEmpty();
  }

  private void removeUserCountryAssociation(UserCountry userCountry) {
    userCountryRepository.delete(userCountry);
  }

  private void checkChatIsNotPrivate(EventRequest request, Chat chat, Long authorId) {
    if (chat.getChatType().equals(ChatType.PRIVATE)
        && chat.getUsers().size() >= MAX_USERS_IN_PRIVATE_CHAT) {
      throw new PrivateChatMustContainTwoUsersException(request, authorId);
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

  private void checkUserAlreadyJoinedChat(EventRequest request, Long authorId) {
    Optional<UserChat> userChats =
        userChatRepository.findByChatIdAndUserId(request.chatId(), authorId);
    if (userChats.isPresent()) {
      throw new UserAlreadyJoinTheChatException(authorId, request.chatId());
    }
  }

  private Chat getChat(EventRequest request, Long authorId) {
    try {
      return chatRepository
          .findById(request.chatId())
          .orElseThrow(() -> new ChatNotFoundException(request.chatId()));
    } catch (ChatNotFoundException e) {
      throw new WebSocketException(e, authorId);
    }
  }

  private void throwIfChatNotExists(EventRequest request, Long authorId) {
    if (!chatRepository.existsById(request.chatId())) {
      try {
        throw new ChatNotFoundException(request.chatId());
      } catch (ChatNotFoundException e) {
        throw new WebSocketException(e, authorId);
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
