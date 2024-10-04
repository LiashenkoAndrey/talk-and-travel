package online.talkandtravel.service.impl;

import static java.lang.String.format;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.MainCountryChatNotFoundException;
import online.talkandtravel.exception.chat.PrivateChatAlreadyExistsException;
import online.talkandtravel.exception.country.CountryNotFoundException;
import online.talkandtravel.exception.message.MessageFromAnotherChatException;
import online.talkandtravel.exception.message.MessageNotFoundException;
import online.talkandtravel.exception.user.UserChatNotFoundException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.chat.ChatInfoDto;
import online.talkandtravel.model.dto.chat.NewChatDto;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatInfoDto;
import online.talkandtravel.model.dto.chat.SetLastReadMessageRequest;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.ChatType;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.model.entity.UserCountry;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.CountryRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.repository.UserCountryRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.util.mapper.ChatMapper;
import online.talkandtravel.util.mapper.MessageMapper;
import online.talkandtravel.util.mapper.UserChatMapper;
import online.talkandtravel.util.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ChatService} for managing chat operations.
 *
 * <p>This service handles various operations related to chats, including:
 *
 * <ul>
 *   <li>{@link #createPrivateChat(NewPrivateChatDto)} - creates private chat between two users
 *   <li>{@link #findAllUsersPrivateChats()} - finds all private chats of a user
 *   <li>{@link #setLastReadMessage(Long, SetLastReadMessageRequest)} - updates lastReadMessage of
 *       field that represents last read message of chat by user
 *   <li>{@link #findReadMessages(Long, Pageable)} - finds messages that the user has already read
 *   <li>{@link ChatService#findUnreadMessages(Long, Pageable)} - finds messages that the user has
 *       not yet read
 *   <li>{@link #findAllGroupChats(Pageable)} - Retrieves all chats with pagination.
 *   <li>{@link #findMainChat(String)} - Finds the main chat associated with a given country name.
 *   <li>{@link #countUsersInChat(Long)} - Counts the number of users in a specified chat.
 *   <li>{@link #findUserChats()} - Retrieves a list of chats associated with a specific user.
 *   <li>{@link #findAllUsersByChatId(Long)} - Retrieves a list of basic user details for all users
 *       in a specified chat.
 *   <li>{@link #findAllMessagesInChatOrdered(Long, Pageable)} - Retrieves all messages in a
 *       specified chat, ordered and paginated.
 *   <li>{@link #getChat(Long)} - Retrieves a chat entity by its ID, or throws an exception if not
 *       found.
 *   <li>{@link #getCountry(String)} - Retrieves a country entity by its name, or throws an
 *       exception if not found.
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ChatServiceImpl implements ChatService {

  private final ChatRepository chatRepository;
  private final UserChatRepository userChatRepository;
  private final CountryRepository countryRepository;
  private final UserCountryRepository userCountryRepository;
  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;
  private final ChatMapper chatMapper;
  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final UserChatMapper userChatMapper;
  private final AuthenticationService authenticationService;

  private static final Long DEFAULT_UNREAD_MESSAGES_AMOUNT = 0L;

  @Transactional
  @Override
  public ChatDto createCountryChat(NewChatDto dto) {
    User user = authenticationService.getAuthenticatedUser();
    Chat chat = createAndSaveChatWithUser(dto, user);
    Long unreadMessagesCount = countUnreadMessages(user.getId(), chat.getId());
    return chatMapper.toDto(chat, unreadMessagesCount);
  }

  /**
   * creates private chat between two users
   *
   * @param dto dto
   * @return chat id
   */
  @Override
  @Transactional
  public Long createPrivateChat(NewPrivateChatDto dto) {
    User user = authenticationService.getAuthenticatedUser();
    User companion = getUser(dto.companionId());

    checkIfChatExists(user, companion);
    checkIsDifferentUsers(user, companion);
    Chat privateChat = createAndSavePrivateChat(user, companion);
    saveUserChat(privateChat, user);
    saveUserChat(privateChat, companion);
    return privateChat.getId();
  }

  @Override
  public ChatDto findChatById(Long chatId) {
    User user = authenticationService.getAuthenticatedUser();
    Chat chat = getChat(chatId);
    Long unreadMessagesCount = countUnreadMessages(user.getId(), chatId);
    return chatMapper.toDto(chat, unreadMessagesCount);
  }

  @Override
  public List<PrivateChatDto> findAllUsersPrivateChats() {
    User user = authenticationService.getAuthenticatedUser();
    List<UserChat> userChats = userChatRepository.findAllByUserId(user.getId());
    return userChats.stream()
        .filter(userChat -> isPrivateChat(userChat.getChat()))
        .map((userChat) -> buildPrivateChatDto(userChat, user))
        .map(chatNameToCompanionName())
        .toList();
  }

  @Override
  public List<ChatDto> findAllUserPublicChats() {
    return userChatRepository.findAll().stream()
        .filter((userChat -> userChat.getChat().getChatType().equals(ChatType.GROUP)))
        .map((userChat) -> chatMapper.toDto(userChat.getChat(), countUnreadMessages(userChat)))
        .toList();
  }

  @Override
  public void setLastReadMessage(Long chatId, SetLastReadMessageRequest dtoRequest) {
    User user = authenticationService.getAuthenticatedUser();
    UserChat userChat = getUserChat(chatId, user.getId());
    Message message = getMessage(dtoRequest.lastReadMessageId());

    verifyMessageBelongsToChat(message, chatId);

    userChat.setLastReadMessage(message);
    userChatRepository.save(userChat);
  }

  @Override
  public Page<MessageDto> findReadMessages(Long chatId, Pageable pageable) {
    User user = authenticationService.getAuthenticatedUser();
    UserChat userChat = getUserChat(chatId, user.getId());
    Optional<Message> lastReadMessageOpt = Optional.ofNullable(userChat.getLastReadMessage());

    return lastReadMessageOpt.map(
            (lastReadMsg) -> messageRepository.findAllByChatIdAndCreationDateLessThanEqual(chatId,
                lastReadMsg.getCreationDate(), pageable))
        .orElseGet(() -> messageRepository.findAllByChatId(chatId, pageable))
        .map(messageMapper::toMessageDto);
  }

  @Override
  public Page<MessageDto> findUnreadMessages(Long chatId, Pageable pageable) {
    User user = authenticationService.getAuthenticatedUser();
    UserChat userChat = getUserChat(chatId, user.getId());
    Optional<Message> lastReadMessageOpt = Optional.ofNullable(userChat.getLastReadMessage());

    return lastReadMessageOpt.map(
            lastReadMsg -> messageRepository.findAllByChatIdAndCreationDateAfter(chatId,
                    lastReadMsg.getCreationDate(), pageable)
                .map(messageMapper::toMessageDto))
        .orElseGet(Page::empty);
  }

  @Override
  public Page<ChatInfoDto> findAllGroupChats(Pageable pageable) {
    return chatRepository
        .findAllByChatType(ChatType.GROUP, pageable)
        .map(chatMapper::toChatInfoDto);
  }

  @Override
  public ChatDto findMainChat(String countryName) {
    User user = authenticationService.getAuthenticatedUser();
    Country country = getCountry(countryName);
    Optional<Chat> optionalChat =
        country.getChats().stream().filter(chat -> chat.getName().equals(countryName)).findFirst();

    Chat chat = optionalChat.orElseThrow(() -> new MainCountryChatNotFoundException(countryName));
    chat.getMessages().sort(Comparator.comparing(Message::getCreationDate));
    Long unreadMessagesCount = countUnreadMessages(user.getId(), chat.getId());

    return chatMapper.toDto(chat, unreadMessagesCount);
  }

  @Override
  public Long countUsersInChat(Long chatId) {
    Chat chat = getChat(chatId);
    return (long) chat.getUsers().size();
  }

  @Override
  public List<ChatInfoDto> findUserChats() {
    User user = authenticationService.getAuthenticatedUser();
    List<UserChat> userChats = userChatRepository.findAllByUserId(user.getId());
    return userChats.stream().map(chatMapper::userChatToChatInfoDto).toList();
  }

  @Override
  public List<UserDtoBasic> findAllUsersByChatId(Long chatId) {
    Chat chat = getChat(chatId);
    return chat.getUsers().stream().map(userMapper::toUserDtoBasic).toList();
  }

  @Override
  public Page<MessageDto> findAllMessagesInChatOrdered(Long chatId, Pageable pageable) {
    return messageRepository
        .findAllByChatId(chatId, pageable)
        .map(messageMapper::toMessageDto);
  }

  private UserChat getUserChat(Long chatId, Long userId) {
    return userChatRepository
        .findByChatIdAndUserId(chatId, userId)
        .orElseThrow(() -> new UserChatNotFoundException(chatId, userId));
  }

  private Long countUnreadMessages(Long userId, Long chatId) {
    return userChatRepository.findByChatIdAndUserId(chatId, userId)
        .map(this::countUnreadMessages)
        .orElse(DEFAULT_UNREAD_MESSAGES_AMOUNT);
  }

  private Long countUnreadMessages(UserChat userChat) {
    Optional<Message> lastReadMessage = Optional.ofNullable(userChat.getLastReadMessage());
    return lastReadMessage.map((msg) -> messageRepository.countAllByChatIdAndCreationDateAfter(
            msg.getChat().getId(), msg.getCreationDate()))
        .orElse(DEFAULT_UNREAD_MESSAGES_AMOUNT);
  }

  private Message getMessage(Long id) {
    return messageRepository.findById(id).orElseThrow(
        () -> new MessageNotFoundException(id));
  }

  private void verifyMessageBelongsToChat(Message message, Long chatId) {
    if (!message.getChat().getId().equals(chatId)) {
      throw new MessageFromAnotherChatException(message.getId(), chatId, message.getChat().getId());
    }
  }

  private PrivateChatDto buildPrivateChatDto(UserChat authUserChat, User authUser) {
    Chat chat = authUserChat.getChat();
    UserChat companionUserChat = getCompanionUserChat(authUser.getId(), chat);

    PrivateChatInfoDto privateChatInfoDto = chatMapper.chatToPrivateChatInfoDto(chat, countUnreadMessages(authUserChat));
    Message lastMessage = messageRepository.findFirstByChatIdOrderByCreationDateDesc(chat.getId()).orElse(null);
    return userChatMapper.toPrivateChatDto(privateChatInfoDto, companionUserChat.getUser(), lastMessage);
  }

  private boolean isPrivateChat(Chat chat) {
    return chat.getChatType().equals(ChatType.PRIVATE);
  }

  private UserChat getCompanionUserChat(Long authUserId, Chat chat) {
    return userChatRepository.findAllByChatId(chat.getId()).stream()
        .filter(userChat1 -> !userChat1.getUser().getId().equals(authUserId))
        .findFirst()
        .orElse(UserChat.builder().chat(chat).user(getRemovedUser()).build());
  }


  private Chat getChat(Long chatId) {
    return chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException(chatId));
  }

  private User getUser(Long userId) {
    return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
  }

  private Country getCountry(String countryName) {
    return countryRepository
        .findById(countryName)
        .orElseThrow(() -> new CountryNotFoundException(countryName));
  }

  private Chat createAndSavePrivateChat(User user, User companion) {
    String companionName = companion.getUserName();
    String userName = user.getUserName();
    return chatRepository.save(
        Chat.builder()
            .chatType(ChatType.PRIVATE)
            .description(format("Private chat for %s and %s", userName, companionName))
            .name(userName + "-" + companionName)
            .build());
  }

  private Chat createAndSaveChatWithUser(NewChatDto dto, User user) {
    Country country = getCountry(dto.countryId());
    Chat chat = fromChatDto(dto, user, country);
    chat = chatRepository.save(chat);

    saveConnections(user, country, chat);

    return chat;
  }

  private Chat fromChatDto(NewChatDto dto, User user, Country country) {
    return Chat.builder()
        .chatType(ChatType.GROUP)
        .description(dto.description())
        .name(dto.name())
        .country(country)
        .users(List.of(user))
        .build();
  }

  private void saveUserChat(Chat chat, User user) {
    userChatRepository.save(UserChat.builder().chat(chat).user(user).build());
  }

  private Function<PrivateChatDto, PrivateChatDto> chatNameToCompanionName() {
    return oldChat -> new PrivateChatDto(renameChat(oldChat), oldChat.companion(), oldChat.lastMessage());
  }

  private PrivateChatInfoDto renameChat(PrivateChatDto oldChat) {
    return new PrivateChatInfoDto(
        oldChat.chat().id(),
        oldChat.companion().getUserName(),
        oldChat.chat().description(),
        oldChat.chat().chatType(),
        oldChat.chat().creationDate(),
        oldChat.chat().usersCount(),
        oldChat.chat().messagesCount(),
        oldChat.chat().unreadMessagesCount());
  }

  private void checkIfChatExists(User user, User companion) {
    List<Long> participantIds = List.of(user.getId(), companion.getId());
    Optional<Chat> optionalChat =
        chatRepository.findChatByUsersAndChatType(participantIds, ChatType.PRIVATE);
    if (optionalChat.isPresent()) {
      throw new PrivateChatAlreadyExistsException(participantIds);
    }
  }

  private void checkIsDifferentUsers(User user, User companion) {
    if (user.getId().equals(companion.getId())) {
      throw new IllegalArgumentException(
          "Creation a chat with the same user. User id: %s".formatted(user.getId()));
    }
  }

  private void saveConnections(User user, Country country, Chat chat) {
    Optional<UserCountry> userCountryOptional =
        userCountryRepository.findByCountryNameAndUserId(country.getName(), user.getId());

    UserCountry userCountry =
        userCountryOptional.orElseGet(
            () -> UserCountry.builder().country(country).user(user).build());
    Optional<UserChat> optionalUserChat =
        userChatRepository.findByChatIdAndUserId(chat.getId(), user.getId());

    UserChat userChat =
        optionalUserChat.orElse(
            UserChat.builder().chat(chat).user(user).userCountry(userCountry).build());
    userChat.setUserCountry(userCountry);
    userCountry.getChats().add(userChat);

    // save connection user with country
    userCountryRepository.save(userCountry);
  }

  private User getRemovedUser() {
    return User.builder()
        .id(null)
        .userName("user left the chat")
        .about("user left the chat")
        .userEmail("undefined")
        .build();
  }
}
