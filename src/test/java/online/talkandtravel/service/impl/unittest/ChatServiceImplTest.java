package online.talkandtravel.service.impl.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.MainCountryChatNotFoundException;
import online.talkandtravel.exception.chat.PrivateChatAlreadyExistsException;
import online.talkandtravel.exception.country.CountryNotFoundException;
import online.talkandtravel.exception.user.UserChatNotFoundException;
import online.talkandtravel.exception.user.UserNotAuthenticatedException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.chat.NewChatDto;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatInfoDto;
import online.talkandtravel.model.dto.chat.SetLastReadMessageRequest;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.enums.ChatType;
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
import online.talkandtravel.service.impl.ChatServiceImpl;
import online.talkandtravel.util.mapper.ChatMapper;
import online.talkandtravel.util.mapper.MessageMapper;
import online.talkandtravel.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@Log4j2
class ChatServiceImplTest {

  @Mock private ChatRepository chatRepository;
  @Mock private UserChatRepository userChatRepository;
  @Mock private UserCountryRepository userCountryRepository;
  @Mock private CountryRepository countryRepository;
  @Mock private MessageRepository messageRepository;
  @Mock private MessageMapper messageMapper;
  @Mock private ChatMapper chatMapper;
  @Mock private UserMapper userMapper;
  @Mock private UserRepository userRepository;

  @Mock private AuthenticationService authenticationService;

  @InjectMocks ChatServiceImpl underTest;

  private Country country;
  private Chat chat;
  private UserChat userChat;
  private User user;
  private PrivateChatInfoDto privateChatInfoDto;
  private UserDtoBasic userDtoBasic;
  private Message message;
  private MessageDtoBasic messageDtoBasic;
  private Pageable pageable;

  private static final Long USER_ID = 1L;


  @BeforeEach
  void setUp() {
    chat = new Chat();
    chat.setId(1L);
    chat.setName("TestCountry");

    userChat = new UserChat();
    userChat.setChat(chat);
    user = User.builder()
        .id(USER_ID)
        .build();

    privateChatInfoDto =
        new PrivateChatInfoDto(
            1L,
            "TestCountry",
            "Test Chat Description",
            ChatType.GROUP,
            LocalDateTime.now(),
            10L,
            0L);

    country = new Country();
    country.setName("TestCountry");
    country.setChats(List.of(chat));

    userDtoBasic = new UserDtoBasic(1L, "testUser", "Test User", "test@example.com");

    message = new Message();
    message.setId(1L);
    message.setContent("Test message");

    messageDtoBasic = new MessageDtoBasic(1L, "Test message", LocalDateTime.now(), 1L, 1L, null);

    pageable = PageRequest.of(0, 10);
  }

  @Test
  void findMainChat_shouldThrow_whenCountryNotFound() {
    String countryName = "NonExistentCountry";
    when(countryRepository.findById(countryName)).thenReturn(Optional.empty());

    assertThrows(CountryNotFoundException.class, () -> underTest.findMainChat(countryName));
    verify(countryRepository, times(1)).findById(countryName);
  }

  @Test
  void findMainChat_shouldThrow_whenMainChatNotFound() {
    String countryName = "TestCountry";
    country.setChats(Collections.emptyList());
    when(countryRepository.findById(countryName)).thenReturn(Optional.of(country));

    assertThrows(MainCountryChatNotFoundException.class, () -> underTest.findMainChat(countryName));
    verify(countryRepository, times(1)).findById(countryName);
  }

  @Test
  void findMainChat_shouldFoundMainChat_whenValid() {
    String countryName = "TestCountry";
    ChatDto chatDto =
        new ChatDto(
            1L,
            "TestCountry",
            "Description of TestCountry",
            new CountryInfoDto(countryName, "tc"),
            ChatType.GROUP,
            LocalDateTime.now(),
            100L,
            Collections.emptyList());

    when(countryRepository.findById(countryName)).thenReturn(Optional.of(country));
    when(chatMapper.toDto(chat)).thenReturn(chatDto);

    ChatDto result = underTest.findMainChat(countryName);

    assertEquals(chatDto, result);
    verify(countryRepository, times(1)).findById(countryName);
    verify(chatMapper, times(1)).toDto(chat);
  }

  @Test
  void findUserChats_shouldReturnEmptyList_whenNoChatsFound() {
    when(userChatRepository.findAllByUserId(USER_ID)).thenReturn(List.of());
    when(authenticationService.getAuthenticatedUser()).thenReturn(user);

    List<PrivateChatInfoDto> result = underTest.findUserChats();

    assertTrue(result.isEmpty());
    verify(userChatRepository, times(1)).findAllByUserId(USER_ID);
    verifyNoInteractions(chatMapper); // Ensure chatMapper is not called
  }

  @Test
  void findUserChats_shouldReturnChatList_whenChatsFound() {
    when(authenticationService.getAuthenticatedUser()).thenReturn(user);
    when(userChatRepository.findAllByUserId(USER_ID)).thenReturn(List.of(userChat));
    when(chatMapper.userChatToPrivateChatInfoDto(userChat)).thenReturn(privateChatInfoDto);

    List<PrivateChatInfoDto> result = underTest.findUserChats();

    assertEquals(1, result.size());
    assertEquals(privateChatInfoDto, result.get(0));
    verify(userChatRepository, times(1)).findAllByUserId(USER_ID);
    verify(chatMapper, times(1)).userChatToPrivateChatInfoDto(userChat);
  }

  @Test
  void findAllUsersByChatId_shouldThrow_whenChatNotFound() {
    Long chatId = 1L;
    when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

    assertThrows(ChatNotFoundException.class, () -> underTest.findAllUsersByChatId(chatId));
    verify(chatRepository, times(1)).findById(chatId);
  }

  @Test
  void findAllUsersByChatId_shouldReturnEmptyList_whenNoUsersFound() {
    Long chatId = 1L;
    chat.setUsers(List.of());
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

    List<UserDtoBasic> result = underTest.findAllUsersByChatId(chatId);

    assertTrue(result.isEmpty());
    verify(chatRepository, times(1)).findById(chatId);
  }

  @Test
  void findAllUsersByChatId_shouldReturnUserList_whenUsersFound() {
    Long chatId = 1L;
    chat.setUsers(List.of(user));
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
    when(userMapper.toUserDtoBasic(user)).thenReturn(userDtoBasic);

    List<UserDtoBasic> result = underTest.findAllUsersByChatId(chatId);

    assertEquals(1, result.size());
    assertEquals(userDtoBasic, result.get(0));
    verify(chatRepository, times(1)).findById(chatId);
    verify(userMapper, times(1)).toUserDtoBasic(user);
  }

  @Test
  void findAllMessagesInChatOrdered_shouldReturnEmptyPage_whenNoMessagesFound() {
    Long chatId = 1L;
    when(messageRepository.findAllByChatId(chatId, pageable)).thenReturn(Page.empty());

    Page<MessageDtoBasic> result = underTest.findAllMessagesInChatOrdered(chatId, pageable);

    assertTrue(result.isEmpty());
    verify(messageRepository, times(1)).findAllByChatId(chatId, pageable);
    verifyNoInteractions(messageMapper); // Ensure messageMapper is not called
  }

  @Test
  void findAllMessagesInChatOrdered_shouldReturnMessagesPage_whenMessagesFound() {
    Long chatId = 1L;
    Page<Message> messagePage = new PageImpl<>(List.of(message), pageable, 1);
    when(messageRepository.findAllByChatId(chatId, pageable)).thenReturn(messagePage);
    when(messageMapper.toMessageDtoBasic(message)).thenReturn(messageDtoBasic);

    Page<MessageDtoBasic> result = underTest.findAllMessagesInChatOrdered(chatId, pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(messageDtoBasic, result.getContent().get(0));
    verify(messageRepository, times(1)).findAllByChatId(chatId, pageable);
    verify(messageMapper, times(1)).toMessageDtoBasic(message);
  }

  @Nested
  class CreateCountryChat {
    String countryName = "countryName", description = "desc", chatName = "chatName";
    Country country1 = Country.builder().name(countryName).build();
    User user1 = User.builder().id(1L).build();

    NewChatDto request = new NewChatDto(chatName, description, countryName);

    Chat chat1 =
        Chat.builder()
            .chatType(ChatType.GROUP)
            .description(description)
            .name(chatName)
            .country(country1)
            .users(List.of(user1))
            .build();

    Chat chat2 =
        Chat.builder()
            .id(1L)
            .chatType(ChatType.GROUP)
            .description(description)
            .name(chatName)
            .country(country1)
            .users(List.of(user1))
            .build();

    @Test
    void createCountryChat_shouldReturnChatDto_whenCountryFoundAndUserAuth() {
      ChatDto chatDto = new ChatDto(chatName);
      UserCountry userCountry = new UserCountry();
      UserChat userChat = new UserChat();

      when(authenticationService.getAuthenticatedUser()).thenReturn(user1);
      when(countryRepository.findById(countryName)).thenReturn(Optional.of(country1));
      when(chatRepository.save(chat1)).thenReturn(chat2);
      when(userCountryRepository.findByCountryNameAndUserId(countryName, user1.getId()))
          .thenReturn(Optional.of(userCountry));
      when(userChatRepository.findByChatIdAndUserId(chat2.getId(), user1.getId()))
          .thenReturn(Optional.of(userChat));

      when(chatMapper.toDto(chat2)).thenReturn(chatDto);

      ChatDto result = underTest.createCountryChat(request);

      verify(authenticationService, times(1)).getAuthenticatedUser();
      verify(countryRepository, times(1)).findById(countryName);
      verify(userCountryRepository, times(1))
          .findByCountryNameAndUserId(countryName, user1.getId());
      verify(userChatRepository, times(1)).findByChatIdAndUserId(chat2.getId(), user1.getId());
      verify(chatRepository, times(1)).save(chat1);
      verify(chatMapper, times(1)).toDto(chat2);

      assertEquals(chatDto, result);
    }

    @Test
    void createCountryChat_shouldReturnChatDto_whenNoCountryFound() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(countryRepository.findById(countryName)).thenReturn(Optional.empty());

      assertThrows(CountryNotFoundException.class, () -> underTest.createCountryChat(request));

      verify(countryRepository, times(1)).findById(countryName);
      verify(authenticationService, times(1)).getAuthenticatedUser();
    }

    @Test
    void createCountryChat_shouldReturnChatDto_whenUserNotAuth() {
      when(authenticationService.getAuthenticatedUser())
          .thenThrow(UserNotAuthenticatedException.class);
      assertThrows(UserNotAuthenticatedException.class, () -> underTest.createCountryChat(null));
    }
  }

  @Nested
  class PrivateChat {
    private final Long userId = 1L, companionId = 2L;
    private final NewPrivateChatDto dto = new NewPrivateChatDto(companionId);
    private final List<Long> participantIds = List.of(userId, companionId);

    @Test
    void createPrivateChat_shouldReturnChatId_whenUserAndCompanionExist() {
      User companion = createUserWithId(companionId);

      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      whenUserRepoFindById(companionId, Optional.of(companion));
      whenChatRepoFindPrivateChatByParticipants(participantIds, Optional.empty());
      when(chatRepository.save(any(Chat.class))).thenReturn(chat);

      Long result = underTest.createPrivateChat(dto);
      assertEquals(1, result);
      verifyCallsUserRepoFindById(1, companionId);
    }

    @Test
    void createPrivateChat_shouldThrow_whenNoCompanionFound() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);

      assertThrows(UserNotFoundException.class, () -> underTest.createPrivateChat(dto));
    }

    @Test
    void createPrivateChat_shouldThrow_whenNoUserFound() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      whenUserRepoFindById(companionId, Optional.empty());

      assertThrows(UserNotFoundException.class, () -> underTest.createPrivateChat(dto));
    }

    @Test
    void createPrivateChat_shouldThrow_whenChatAlreadyExists() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      whenUserRepoFindById(companionId, Optional.of(createUserWithId(2L)));
      whenChatRepoFindPrivateChatByParticipants(participantIds, Optional.of(chat));

      assertThrows(PrivateChatAlreadyExistsException.class, () -> underTest.createPrivateChat(dto));
    }
  }

  private User createUserWithId(Long id) {
    return User.builder().id(id).build();
  }

  private void whenUserRepoFindById(Long id, Optional<User> thenReturn) {
    when(userRepository.findById(id)).thenReturn(thenReturn);
  }

  private void verifyCallsUserRepoFindById(int times, Long id) {
    verify(userRepository, times(times)).findById(id);
  }

  private void whenChatRepoFindPrivateChatByParticipants(
      List<Long> participantsIds, Optional<Chat> thenReturn) {
    when(chatRepository.findChatByUsersAndChatType(participantsIds, ChatType.PRIVATE))
        .thenReturn(thenReturn);
  }

  @Nested
  class SetLastReadMessage {
    private final Long chatId = 1L, userId = 1L, lastReadMessageId = 2L;
    private final UserChat userChat1 = new UserChat();
    private final SetLastReadMessageRequest requestDto =
        new SetLastReadMessageRequest(lastReadMessageId);

    @Test
    void setLastReadMessage_shouldUpdateField_whenUserChatFound() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository.findByChatIdAndUserId(chatId, userId))
          .thenReturn(Optional.of(userChat1));

      underTest.setLastReadMessage(chatId, requestDto);

      verify(userChatRepository, times(1)).save(userChat1);
    }

    @Test
    void setLastReadMessage_shouldThrow_whenNoUserChatFound() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.empty());

      assertThrows(
          UserChatNotFoundException.class, () -> underTest.setLastReadMessage(chatId, requestDto));
    }
  }

  @Nested
  class FindReadAndUnreadMessages {
    private final Long chatId = 1L, lastReadMessageId = 1L;
    private final Pageable pageable1 = PageRequest.of(0, 10);
    private final String content = "test";
    private final Page<MessageDtoBasic> page =
        new PageImpl<>(List.of(new MessageDtoBasic(content)));

    @Test
    void findReadMessages_shouldReturnNotEmptyList_whenMessagesFound() {
      user.setId(1L);
      userChat.setLastReadMessageId(1L);
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository
          .findByChatIdAndUserId(chatId, user.getId())).thenReturn(Optional.ofNullable(userChat));

      when(messageRepository.findAllByChatIdAndIdLessThanEqual(chatId, lastReadMessageId, pageable))
          .thenReturn(page);
      Page<MessageDtoBasic> result =
          underTest.findReadMessages(chatId, pageable1);
      assertEquals(content, result.toList().get(0).content());
    }

    @Test
    void findUnreadMessages_shouldReturnNotEmptyList_whenMessagesFound() {
      user.setId(1L);
      userChat.setLastReadMessageId(1L);
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository
          .findByChatIdAndUserId(chatId, user.getId())).thenReturn(Optional.ofNullable(userChat));
      when(messageRepository.findAllByChatIdAndIdAfter(chatId, lastReadMessageId, pageable))
          .thenReturn(page);
      Page<MessageDtoBasic> result =
          underTest.findUnreadMessages(chatId, pageable1);
      assertEquals(content, result.toList().get(0).content());
    }
  }
}
