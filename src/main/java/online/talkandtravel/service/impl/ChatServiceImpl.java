package online.talkandtravel.service.impl;

import static java.lang.String.format;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.MainCountryChatNotFoundException;
import online.talkandtravel.exception.country.CountryNotFoundException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.chat.ChatInfoDto;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.ChatType;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.CountryRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.util.mapper.ChatMapper;
import online.talkandtravel.util.mapper.MessageMapper;
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
 *   <li>{@link #findAllChats(Pageable)} - Retrieves all chats with pagination.
 *   <li>{@link #findMainChat(String)} - Finds the main chat associated with a given country name.
 *   <li>{@link #countUsersInChat(Long)} - Counts the number of users in a specified chat.
 *   <li>{@link #findUserChats(Long)} - Retrieves a list of chats associated with a specific user.
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
public class ChatServiceImpl implements ChatService {

  private final ChatRepository chatRepository;
  private final UserChatRepository userChatRepository;
  private final CountryRepository countryRepository;
  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;
  private final ChatMapper chatMapper;
  private final UserMapper userMapper;
  private final UserRepository userRepository;

  /**
   * creates private chat between two users
   * @param dto dto
   * @return chat id
   */
  @Override
  @Transactional
  public Long createPrivateChat(NewPrivateChatDto dto) {
    User user = getUser(dto.userId());
    User companion = getUser(dto.companionId());
    Chat privateChat = createAndSavePrivateChat(user, companion);

    saveUserChat(privateChat, user);
    saveUserChat(privateChat,companion);
    return privateChat.getId();
  }

  @Override
  public Page<ChatInfoDto> findAllChats(Pageable pageable) {
    return chatRepository.findAll(pageable).map(chatMapper::toInfoDto);
  }

  @Override
  public ChatDto findMainChat(String countryName) {
    Country country = getCountry(countryName);
    Optional<Chat> optionalChat =
        country.getChats().stream().filter(chat -> chat.getName().equals(countryName)).findFirst();
    Chat chat = optionalChat.orElseThrow(() -> new MainCountryChatNotFoundException(countryName));
    return chatMapper.toDto(chat);
  }

  @Override
  public Long countUsersInChat(Long chatId) {
    Chat chat = getChat(chatId);
    return (long) chat.getUsers().size();
  }

  @Override
  public List<ChatInfoDto> findUserChats(Long userId) {
    List<UserChat> userChats = userChatRepository.findAllByUserId(userId);
    return userChats.stream().map(chatMapper::userChatToInfoDto).toList();
  }

  @Override
  public List<UserDtoBasic> findAllUsersByChatId(Long chatId) {
    Chat chat = getChat(chatId);
    return chat.getUsers().stream().map(userMapper::toUserDtoBasic).toList();
  }

  @Override
  public Page<MessageDtoBasic> findAllMessagesInChatOrdered(Long chatId, Pageable pageable) {
    return messageRepository
        .findAllByChatId(chatId, pageable)
        .map(messageMapper::toMessageDtoBasic);
  }

  private Chat getChat(Long chatId) {
    return chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException(chatId));
  }

  private User getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  private Country getCountry(String countryName) {
    return countryRepository
        .findById(countryName)
        .orElseThrow(() -> new CountryNotFoundException(countryName));
  }

  private Chat createAndSavePrivateChat(User user, User companion) {
    String companionName = companion.getUserName();
    String userName = user.getUserName();
    return chatRepository.save(Chat.builder()
        .chatType(ChatType.PRIVATE)
        .description(format("Private chat for %s and %s", userName, companionName))
        .name(userName + "-" + companionName)
        .build());
  }

  private void saveUserChat(Chat chat, User user) {
    userChatRepository.save(UserChat.builder()
        .chat(chat)
        .user(user)
        .build());
  }
}
