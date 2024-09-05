package online.talkandtravel.service.impl;

import static online.talkandtravel.util.service.EventDestination.CHAT_MESSAGE_DESTINATION;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.PrivateChatMustContainTwoUsersException;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.exception.user.UserAlreadyJoinTheChatException;
import online.talkandtravel.exception.user.UserCountryNotFoundException;
import online.talkandtravel.model.dto.event.EventPayload;
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
import online.talkandtravel.service.event.ChatEventService;
import online.talkandtravel.util.mapper.MessageMapper;
import online.talkandtravel.util.mapper.UserMapper;
import online.talkandtravel.util.service.EventDestination;
import online.talkandtravel.util.service.EventPublisherUtil;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ChatEventService} for managing chat-related events.
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
@Log4j2
@RequiredArgsConstructor
public class ChatEventServiceImpl implements ChatEventService {

  private static final String JOINED_THE_CHAT = "%s joined the chat";
  private static final String LEFT_THE_CHAT = "%s left the chat";
  private static final int MAX_USERS_IN_PRIVATE_CHAT = 2;

  private final ChatRepository chatRepository;
  private final UserChatRepository userChatRepository;
  private final UserCountryRepository userCountryRepository;
  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;
  private final UserMapper userMapper;
  private final EventPublisherUtil publisherUtil;

  @Override
  public void publishEvent(EventPayload payload, Long chatId) {
    String dest = CHAT_MESSAGE_DESTINATION.formatted(chatId);
    publisherUtil.publishEvent(dest, payload);
  }

  @Transactional
  @Override
  public void leaveChat(EventRequest request, Principal principal) {
    log.info("create a new LEAVE CHAT event {}", request);
    User author = getUser(principal);
    Chat chat = getChat(request, author.getId());

    removeConnections(request, chat, author);
    Message message = createAndSaveMessage(LEFT_THE_CHAT, author, chat, MessageType.LEAVE);
    deleteChatIfEmpty(request, principal);

    MessageDto messageDto = messageMapper.toMessageDto(message);
    publishEvent(messageDto, request.chatId());
  }


  @Transactional
  @Override
  public void joinChat(EventRequest request, Principal principal) {
    log.info("create a new JOIN CHAT event {}, {}", request, principal);

    User author = getUser(principal);
    Chat chat = getChat(request, author.getId());
    checkChatIsNotPrivate(request, chat, author.getId());
    checkUserAlreadyJoinedChat(request, author.getId());

    saveConnections(chat, author);

    Message message = createAndSaveMessage(JOINED_THE_CHAT, author, chat, MessageType.JOIN);

    MessageDto messageDto = messageMapper.toMessageDto(message);
    publishEvent(messageDto, request.chatId());
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

  @Override
  public void startTyping(EventRequest request, Principal principal) {
    handleTypingEvent(request, principal, MessageType.START_TYPING);
  }

  @Override
  public void stopTyping(EventRequest request, Principal principal) {
    handleTypingEvent(request, principal, MessageType.STOP_TYPING);
  }

  private void handleTypingEvent(EventRequest request, Principal principal, MessageType messageType) {
    User user = getUser(principal);
    throwIfChatNotExists(request, user.getId());

    EventResponse eventResponse = createChatTransientEvent(user, messageType);
    publishEvent(eventResponse, request.chatId());
  }

  private Message createAndSaveMessage(String content, User author, Chat chat, MessageType leave) {
    Message message =
        Message.builder()
            .content(content.formatted(author.getUserName()))
            .chat(chat)
            .sender(author)
            .type(leave)
            .build();
    message = messageRepository.save(message);
    return message;
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
