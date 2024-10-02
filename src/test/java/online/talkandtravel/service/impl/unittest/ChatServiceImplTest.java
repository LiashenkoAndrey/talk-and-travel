package online.talkandtravel.service.impl.unittest;

import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static online.talkandtravel.testdata.UserTestData.getTomas;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import online.talkandtravel.model.dto.chat.ChatInfoDto;
import online.talkandtravel.model.dto.chat.NewChatDto;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatInfoDto;
import online.talkandtravel.model.dto.chat.SetLastReadMessageRequest;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoShort;
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
import online.talkandtravel.repository.CountryRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.repository.UserCountryRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.impl.ChatServiceImpl;
import online.talkandtravel.util.mapper.ChatMapper;
import online.talkandtravel.util.mapper.MessageMapper;
import online.talkandtravel.util.mapper.UserChatMapper;
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

  @Mock private UserChatMapper userChatMapper;

  @Mock private AuthenticationService authenticationService;

  @InjectMocks ChatServiceImpl underTest;

  private Country country;
  private Chat chat;
  private UserChat userChat;
  private User user;
  private ChatInfoDto chatInfoDto;
  private UserDtoBasic userDtoBasic;
  private Message message;
  private MessageDto messageDto;
  private Pageable pageable;
  private UserNameDto userNameDto;

  private static final Long USER_ID = 1L;
  private static final String USER_NAME = "Tomas";


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

    chatInfoDto =
        new ChatInfoDto(
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

    userNameDto = new UserNameDto(USER_ID, USER_NAME);
    messageDto = new MessageDto(1L, MessageType.TEXT, "Test message", LocalDateTime.now(), userNameDto, 1L, null);

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

    List<ChatInfoDto> result = underTest.findUserChats();

    assertTrue(result.isEmpty());
    verify(userChatRepository, times(1)).findAllByUserId(USER_ID);
    verifyNoInteractions(chatMapper); // Ensure chatMapper is not called
  }

  @Test
  void findUserChats_shouldReturnChatList_whenChatsFound() {
    when(authenticationService.getAuthenticatedUser()).thenReturn(user);
    when(userChatRepository.findAllByUserId(USER_ID)).thenReturn(List.of(userChat));
    when(chatMapper.userChatToChatInfoDto(userChat)).thenReturn(chatInfoDto);

    List<ChatInfoDto> result = underTest.findUserChats();

    assertEquals(1, result.size());
    assertEquals(chatInfoDto, result.get(0));
    verify(userChatRepository, times(1)).findAllByUserId(USER_ID);
    verify(chatMapper, times(1)).userChatToChatInfoDto(userChat);
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

    Page<MessageDto> result = underTest.findAllMessagesInChatOrdered(chatId, pageable);

    assertTrue(result.isEmpty());
    verify(messageRepository, times(1)).findAllByChatId(chatId, pageable);
    verifyNoInteractions(messageMapper); // Ensure messageMapper is not called
  }

  @Test
  void findAllMessagesInChatOrdered_shouldReturnMessagesPage_whenMessagesFound() {
    Long chatId = 1L;
    Page<Message> messagePage = new PageImpl<>(List.of(message), pageable, 1);
    when(messageRepository.findAllByChatId(chatId, pageable)).thenReturn(messagePage);
    when(messageMapper.toMessageDto(message)).thenReturn(messageDto);

    Page<MessageDto> result = underTest.findAllMessagesInChatOrdered(chatId, pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(messageDto, result.getContent().get(0));
    verify(messageRepository, times(1)).findAllByChatId(chatId, pageable);
    verify(messageMapper, times(1)).toMessageDto(message);
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
  class CreatePrivateChat {
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
  class FindAllUsersPrivateChats {

    private final User alice = getAlice(), bob = getBob();
    private final Chat arubaChat = buildChat(1L, ChatType.GROUP, "Aruba chat", "Aruba", List.of(alice, bob, getTomas()));
    private final Chat aliseBobChat = buildChat(200L, ChatType.PRIVATE, "Private chat for Alice and Bob", "Alice-Bob", List.of(alice, bob));
    private final Chat aliseAndDeletedChat = buildChat(201L, ChatType.PRIVATE, "Private chat for Alice and user left the chat", "Alice-user left the chat", List.of(alice));


    private static final String REMOVED_USER_NAME = "user left the chat";
    private static final String REMOVED_USER_EMAIL = "undefined";
    private static final String REMOVED_USER_ABOUT = "user left the chat";

    private final String lastAliceBobChatMessageContent = "hello alice how's it going?";
    private final MessageDto lastAliceBobChatMessageDto = new MessageDto(lastAliceBobChatMessageContent);

    private final Message lastAliceBobChatMessage = buildMessage(aliseBobChat, bob, 2L, lastAliceBobChatMessageContent);
    private final UserChat aliseArubaUserChat = buildUserChat(1L, alice, arubaChat);
    private final UserChat aliseBobUserChat = buildUserChat(2L, alice, aliseBobChat);
    private final UserChat bobAliseUserChat = buildUserChat(3L, bob, aliseBobChat);
    private final UserChat aliseAndDeletedUserChat = buildUserChat(4L, alice, aliseAndDeletedChat);

    private final List<UserChat> userChats = List.of(aliseArubaUserChat, aliseBobUserChat, aliseAndDeletedUserChat);

    @BeforeEach
    void init() {
      List<Message> aliseBobChatMessages = List.of(
          buildMessage(aliseBobChat, alice, 1L, "hi Bob!"),
          lastAliceBobChatMessage
      );
      aliseBobChat.setMessages(aliseBobChatMessages);
    }

    @Test
    void shouldReturnList() {

      PrivateChatInfoDto privateChatInfoDto = createPrivateChatInfoDto(bob.getUserName());
      PrivateChatDto aliseBobChatDto = new PrivateChatDto(privateChatInfoDto, new UserDtoShort(bob.getId(), bob.getUserName(), bob.getUserEmail()), null, lastAliceBobChatMessageDto);

      PrivateChatInfoDto privateChatAliceAndDeletedInfoDto = createPrivateChatInfoDto(REMOVED_USER_NAME);
      PrivateChatDto aliseAndDeletedChatDto = new PrivateChatDto(privateChatAliceAndDeletedInfoDto, new UserDtoShort(null, REMOVED_USER_NAME, REMOVED_USER_EMAIL), null, null);

      when(authenticationService.getAuthenticatedUser()).thenReturn(alice);
      when(userChatRepository.findAllByUserId(alice.getId())).thenReturn(userChats);
      when(userChatRepository.findAllByChatId(200L)).thenReturn(List.of(aliseBobUserChat, bobAliseUserChat));
      when(userChatRepository.findAllByChatId(201L)).thenReturn(List.of(aliseAndDeletedUserChat));
      when(chatRepository.countUnreadMessages(any(), any())).thenReturn(0L);
      when(userChatMapper.toPrivateChatDto(aliseBobChat, bob, lastAliceBobChatMessage, 0L, null)).thenReturn(aliseBobChatDto);
      when(userChatMapper.toPrivateChatDto(aliseAndDeletedChat, buildRemovedUser(), null,0L, null)).thenReturn(aliseAndDeletedChatDto);

      List<PrivateChatDto> actual = underTest.findAllUsersPrivateChats();

      assertThat(actual).isNotEmpty();
      PrivateChatDto aliseBobChatDtoActual = actual.get(0);
      assertThat(aliseBobChatDtoActual).isEqualTo(aliseBobChatDto);

      PrivateChatDto aliseAndDeletedChatDtoActual = actual.get(1);
      assertThat(aliseAndDeletedChatDtoActual).isEqualTo(aliseAndDeletedChatDto);
    }

    private static Message buildMessage(Chat chat, User sender, Long id, String content) {
      return Message.builder()
          .chat(chat)
          .sender(sender)
          .type(MessageType.TEXT)
          .id(id)
          .content(content)
          .build();
    }

    private static User buildRemovedUser() {
      return User.builder()
          .userName(REMOVED_USER_NAME)
          .userEmail(REMOVED_USER_EMAIL)
          .about(REMOVED_USER_ABOUT)
          .build();
    }

    private static UserChat buildUserChat(Long id, User user, Chat chat) {
      return UserChat.builder()
          .id(id)
          .user(user)
          .chat(chat)
          .build();
    }


    private PrivateChatInfoDto createPrivateChatInfoDto(String chatName) {
      return new PrivateChatInfoDto(null, chatName, null, null, null, null, null, null);
    }

    private static Chat buildChat(Long id, ChatType type, String description, String name, List<User> users) {
      return Chat.builder()
          .id(id)
          .chatType(type)
          .description(description)
          .name(name)
          .users(users)
          .build();
    }

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
    private final Page<Message> page =
        new PageImpl<>(List.of(new Message(content)));

    @Test
    void findReadMessages_shouldReturnNotEmptyList_whenMessagesFound() {
      user.setId(1L);
      userChat.setLastReadMessageId(1L);

      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository.findByChatIdAndUserId(chatId, user.getId()))
          .thenReturn(Optional.ofNullable(userChat));

      List<Message> messages = List.of(new Message( "Read message content"));
      Page<Message> page = new PageImpl<>(messages, pageable1, messages.size());

      when(messageRepository.findAllByChatIdAndIdLessThanEqual(chatId, userChat.getLastReadMessageId(), pageable1))
          .thenReturn(page);

      MessageDto messageDto = new MessageDto("Read message content");
      when(messageMapper.toMessageDto(any(Message.class))).thenReturn(messageDto);

      Page<MessageDto> result = underTest.findReadMessages(chatId, pageable1);

      assertEquals("Read message content", result.toList().get(0).content());
    }


    @Test
    void findUnreadMessages_shouldReturnNotEmptyList_whenMessagesFound() {
      user.setId(1L);
      userChat.setLastReadMessageId(1L);

      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository.findByChatIdAndUserId(chatId, user.getId()))
          .thenReturn(Optional.ofNullable(userChat));

      List<Message> messages = List.of(new Message("Test content"));
      Page<Message> page = new PageImpl<>(messages, pageable1, messages.size());

      when(messageRepository.findAllByChatIdAndIdAfter(chatId, userChat.getLastReadMessageId(), pageable1))
          .thenReturn(page);

      MessageDto messageDto = new MessageDto("Test content");
      when(messageMapper.toMessageDto(any(Message.class))).thenReturn(messageDto);

      Page<MessageDto> result = underTest.findUnreadMessages(chatId, pageable1);

      assertEquals("Test content", result.toList().get(0).content());
    }

  }
}
