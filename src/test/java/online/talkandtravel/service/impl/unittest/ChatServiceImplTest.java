package online.talkandtravel.service.impl.unittest;

import static online.talkandtravel.testdata.ChatTestData.ARUBA_CHAT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.MainCountryChatNotFoundException;
import online.talkandtravel.exception.chat.PrivateChatAlreadyExistsException;
import online.talkandtravel.exception.country.CountryNotFoundException;
import online.talkandtravel.exception.message.MessageNotFoundException;
import online.talkandtravel.exception.user.UserChatNotFoundException;
import online.talkandtravel.exception.user.UserNotAuthenticatedException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.chat.BasicChatInfoDto;
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
import org.mockito.MockitoAnnotations;
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

  private static final Long USER_ID = 1L;
  private static final String USER_NAME = "Tomas";


  @BeforeEach
  void setUp() {
    chat = Chat.builder()
        .id(ARUBA_CHAT_ID)
        .chatType(ChatType.GROUP)
        .name("TestCountry")
        .build();

    userChat = UserChat.builder().chat(chat).build();
    user = User.builder().id(USER_ID).build();

    chatInfoDto =
        new ChatInfoDto(
            1L,
            "TestCountry",
            "Test Chat Description",
            ChatType.GROUP,
            ZonedDateTime.now(ZoneOffset.UTC),
            10L,
            0L);

    country = Country.builder()
        .name("TestCountry")
        .chats(List.of(chat))
        .build();

    userDtoBasic = new UserDtoBasic(1L, "testUser", "Test User", "test@example.com", "url");

    message = Message.builder()
        .chat(chat)
        .id(1L)
        .content("Test message")
        .build();


    UserNameDto userNameDto = new UserNameDto(USER_ID, USER_NAME, "url");
    messageDto = new MessageDto(1L, MessageType.TEXT, "Test message", ZonedDateTime.now(ZoneOffset.UTC),
        userNameDto, 1L, null);

    pageable = PageRequest.of(0, 10);
  }


  @Nested
  class FindMainChat {
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
      Long chatId = 1L;
      Long unreadMessagesCount = 0L;

      ChatDto chatDto =
          new ChatDto(
              chatId,
              "TestCountry",
              "Description of TestCountry",
              new CountryInfoDto(countryName, "tc"),
              ChatType.GROUP,
              ZonedDateTime.now(ZoneOffset.UTC),
              100L,
              0L,
              unreadMessagesCount);

      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(countryRepository.findById(countryName)).thenReturn(Optional.of(country));
      when(userChatRepository.findByChatIdAndUserId(chatId, user.getId())).thenReturn(Optional.empty());
      when(chatMapper.toDto(chat, unreadMessagesCount)).thenReturn(chatDto);

      ChatDto result = underTest.findMainChat(countryName);

      assertEquals(chatDto, result);

      verify(countryRepository, times(1)).findById(countryName);
      verifyNoInteractions(messageRepository);
      verify(chatMapper, times(1)).toDto(chat, unreadMessagesCount);
    }
  }


  @Nested
  class FindUserChats {
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
  }


  @Nested
  class FindAllUsersByChatId {
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
  }

  @Nested
  class FindAllMessagesInChatOrdered {

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
  }

  @Nested
  class CreateCountryChat {

    String countryName = "countryName", description = "desc", chatName = "chatName";
    Country country1 = Country.builder().name(countryName).build();
    User user1 = User.builder().id(1L).build();

    NewChatDto request = new NewChatDto(chatName, description, countryName);

    Chat notSavedChat =
        Chat.builder()
            .chatType(ChatType.GROUP)
            .description(description)
            .name(chatName)
            .country(country1)
            .users(List.of(user1))
            .build();

    Chat savedChat =
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
      userChat.setLastReadMessage(Message.builder().creationDate(ZonedDateTime.now(ZoneOffset.UTC))
          .chat(savedChat).build());

      when(authenticationService.getAuthenticatedUser()).thenReturn(user1);
      when(countryRepository.findById(countryName)).thenReturn(Optional.of(country1));
      when(chatRepository.save(notSavedChat)).thenReturn(savedChat);
      when(userCountryRepository.findByCountryNameAndUserId(countryName, user1.getId()))
          .thenReturn(Optional.of(userCountry));
      when(userChatRepository.findByChatIdAndUserId(savedChat.getId(), user1.getId()))
          .thenReturn(Optional.of(userChat));
      when(messageRepository.countAllByChatIdAndCreationDateAfter(anyLong(), any(ZonedDateTime.class))).thenReturn(100L);
      when(chatMapper.toDto(savedChat, 100L)).thenReturn(chatDto);

      ChatDto result = underTest.createCountryChat(request);

      verify(authenticationService, times(1)).getAuthenticatedUser();
      verify(countryRepository, times(1)).findById(countryName);
      verify(userCountryRepository, times(1))
          .findByCountryNameAndUserId(countryName, user1.getId());
      verify(userChatRepository, times(2)).findByChatIdAndUserId(savedChat.getId(), user1.getId());
      verify(chatRepository, times(1)).save(notSavedChat);
      verify(chatMapper, times(1)).toDto(savedChat, 100L);

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

    private final User alice = getAlice();
    private final User bob = getBob();
    private final Chat aliceBobChat = buildChat(200L, ChatType.PRIVATE,
        "Private chat for Alice and Bob", "Alice-Bob", List.of(alice, bob));
    private final Chat aliceAndDeletedChat = buildChat(201L, ChatType.PRIVATE,
        "Private chat for Alice and user left the chat", "Alice-user left the chat",
        List.of(alice));

    private final Message lastAliceBobChatMessage = buildMessage(aliceBobChat, bob, 2L,
        "hello alice how's it going?");
    private final UserChat aliceBobUserChat = buildUserChat(2L, alice, aliceBobChat, null);
    private final UserChat bobAliceUserChat = buildUserChat(3L, bob, aliceBobChat, null);
    private final UserChat aliceAndDeletedUserChat = buildUserChat(4L, alice, aliceAndDeletedChat, null);

    private final String REMOVED_USER_NAME = "user left the chat";
    private final String REMOVED_USER_EMAIL = "undefined";

    @BeforeEach
    void init() {
      MockitoAnnotations.openMocks(this);
      List<Message> aliceBobChatMessages = List.of(
          buildMessage(aliceBobChat, alice, 1L, "hi Bob!"),
          lastAliceBobChatMessage
      );
      aliceBobChat.setMessages(aliceBobChatMessages);
    }

    @Test
    void shouldReturnList() {
      PrivateChatInfoDto privateChatInfoDto = createPrivateChatInfoDto(bob.getUserName());
      PrivateChatDto aliceBobChatDto = new PrivateChatDto(privateChatInfoDto,
          new UserDtoShort(bob.getId(), bob.getUserName(), bob.getUserEmail(), "url"),
          new MessageDto(lastAliceBobChatMessage.getContent()));

      PrivateChatInfoDto privateChatAliceAndDeletedInfoDto = createPrivateChatInfoDto(
          REMOVED_USER_NAME);
      PrivateChatDto aliceAndDeletedChatDto = new PrivateChatDto(privateChatAliceAndDeletedInfoDto,
          new UserDtoShort(null, REMOVED_USER_NAME, REMOVED_USER_EMAIL, "url"), null);

      when(authenticationService.getAuthenticatedUser()).thenReturn(alice);
      when(userChatRepository.findAllByUserId(alice.getId())).thenReturn(
          List.of(aliceBobUserChat, aliceAndDeletedUserChat));
      when(userChatRepository.findAllByChatId(200L)).thenReturn(
          List.of(aliceBobUserChat, bobAliceUserChat));
      when(userChatRepository.findAllByChatId(201L)).thenReturn(List.of(aliceAndDeletedUserChat));
      when(chatMapper.chatToPrivateChatInfoDto(any(Chat.class), anyLong())).thenReturn(
          privateChatInfoDto, privateChatAliceAndDeletedInfoDto);
      when(messageRepository.findFirstByChatIdOrderByCreationDateDesc(200L)).thenReturn(
          Optional.of(lastAliceBobChatMessage));
      when(userChatMapper.toPrivateChatDto(privateChatInfoDto, bob, lastAliceBobChatMessage))
          .thenReturn(aliceBobChatDto);

      when(messageRepository.findFirstByChatIdOrderByCreationDateDesc(201L)).thenReturn(
          Optional.empty());
      when(userChatMapper.toPrivateChatDto(privateChatAliceAndDeletedInfoDto, User.builder()
                  .userName(REMOVED_USER_NAME)
                  .userEmail("undefined")
                  .about("user left the chat")
              .build(),
          null))
          .thenReturn(aliceAndDeletedChatDto);

      // Act
      List<PrivateChatDto> actual = underTest.findAllUsersPrivateChats();

      // Assert
      assertThat(actual).isNotEmpty();
      assertThat(actual).hasSize(2);

      PrivateChatDto aliceBobChatDtoActual = actual.get(0);
      assertThat(aliceBobChatDtoActual).isEqualTo(aliceBobChatDto);

      PrivateChatDto aliceAndDeletedChatDtoActual = actual.get(1);
      assertThat(aliceAndDeletedChatDtoActual).isEqualTo(aliceAndDeletedChatDto);

      verify(authenticationService).getAuthenticatedUser();
      verify(userChatRepository).findAllByUserId(alice.getId());
      verify(userChatRepository).findAllByChatId(200L);
      verify(userChatRepository).findAllByChatId(201L);
      verify(chatMapper, times(2)).chatToPrivateChatInfoDto(any(Chat.class), anyLong());
      verify(userChatMapper, times(2)).toPrivateChatDto(any(), any(), any());
    }

    @Nested
    class FindAllCountriesMainChats {
      private final Country country = new Country("TestCountry", "flagCode");
      private final Chat customPublicChat = Chat.builder()
          .name("test")
          .country(country)
          .build();

      @Test
      void shouldReturnBasicChatInfoDtoPage() {
        chat.setCountry(country);

        Page<Chat> chatPage = new PageImpl<>(List.of(chat, customPublicChat));
        BasicChatInfoDto basicChatInfoDto = new BasicChatInfoDto(1L, "name", 4L, new CountryInfoDto("name", "flagCode"));
        when(chatRepository.findAllByChatType(ChatType.GROUP, Pageable.ofSize(10))).thenReturn(chatPage);
        when(chatMapper.toBasicChatInfoDto(chat)).thenReturn(basicChatInfoDto);

        Page<BasicChatInfoDto> actual = underTest.findAllCountriesMainChats(Pageable.ofSize(10));

        assertNotNull(actual);
        assertEquals(1, actual.getTotalElements());
        assertEquals(basicChatInfoDto, actual.toList().get(0));

        verify(chatRepository).findAllByChatType(ChatType.GROUP, Pageable.ofSize(10));
        verify(chatMapper).toBasicChatInfoDto(chat);
      }


      @Test
      void shouldReturnBasicChatInfoDtoPage_whenNoElements() {
        Page<Chat> chatPage = new PageImpl<>(List.of());
        when(chatRepository.findAllByChatType(ChatType.GROUP, Pageable.ofSize(10))).thenReturn(chatPage);

        Page<BasicChatInfoDto> actual = underTest.findAllCountriesMainChats(Pageable.ofSize(10));

        assertNotNull(actual);
        assertEquals(0, actual.getTotalElements());

        verify(chatRepository).findAllByChatType(ChatType.GROUP, Pageable.ofSize(10));
        verifyNoInteractions(chatMapper);
      }
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

    private static UserChat buildUserChat(Long id, User user, Chat chat, Message lastReadMessage) {
      return UserChat.builder()
          .id(id)
          .user(user)
          .chat(chat)
          .lastReadMessage(lastReadMessage)
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

    private static User getAlice() {
      return User.builder().id(1L).userName("Alice").userEmail("alice@example.com").build();
    }

    private static User getBob() {
      return User.builder().id(2L).userName("Bob").userEmail("bob@example.com").build();
    }

  }

  @Nested
  class FindAllUserPublicChats {

    List<UserChat> userChats;
    ChatDto chatDto;

    @BeforeEach
    void init() {
      Message lastReadMessage = message;
      lastReadMessage.setCreationDate(ZonedDateTime.now().minusDays(1));

      userChat.setLastReadMessage(lastReadMessage);

      userChats = List.of(userChat);
      chatDto = new ChatDto("Test Group Chat");
    }

    @Test
    void findAllUsersPublicChats_shouldReturnChatDtoList_whenPublicChatsExist() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository.findAllByUserId(1L)).thenReturn(userChats);
      when(messageRepository.countAllByChatIdAndCreationDateAfter(eq(chat.getId()), any(ZonedDateTime.class)))
          .thenReturn(5L);
      when(chatMapper.toDto(any(Chat.class), anyLong())).thenReturn(chatDto);

      List<ChatDto> result = underTest.findAllUserPublicChats();

      assertThat(result).isNotEmpty();
      assertThat(result).hasSize(1);
      assertThat(result.get(0)).isEqualTo(chatDto);

      verify(userChatRepository).findAllByUserId(1L);
      verify(messageRepository).countAllByChatIdAndCreationDateAfter(eq(chat.getId()), any(ZonedDateTime.class));
      verify(chatMapper).toDto(any(Chat.class), anyLong());
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
      when(messageRepository.findById(2L)).thenReturn(Optional.of(message));

      underTest.setLastReadMessage(chatId, requestDto);

      verify(userChatRepository).save(userChat1);
      verify(messageRepository).findById(2L);
    }

    @Test
    void setLastReadMessage_shouldThrow_whenNoUserChatFound() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.empty());

      assertThrows(
          UserChatNotFoundException.class, () -> underTest.setLastReadMessage(chatId, requestDto));
    }

    @Test
    void setLastReadMessage_shouldThrow_whenNoUserMessageFound() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(
          Optional.ofNullable(UserChat.builder().build()));

      assertThrows(
          MessageNotFoundException.class, () -> underTest.setLastReadMessage(chatId, requestDto));
    }
  }

  @Nested
  class FindReadAndUnreadMessages {

    private static final Long CHAT_ID = 1L;
    private static final Long FROM_MESSAGE_ID = 1L;
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);
    private List<Message> messages;
    private Page<Message> messagePage;
    private ZonedDateTime createdOn;

    @BeforeEach
    void setUp() {
      createdOn = ZonedDateTime.now(ZoneOffset.UTC);
      messages = List.of(
              Message.builder()
                  .chat(chat)
                      .id(1L)
                      .creationDate(createdOn)
                      .content("message 1")
                      .build(),
              Message.builder()
                      .chat(chat)
                      .id(2L)
                      .creationDate(createdOn.minusDays(1))
                      .content("message 2")
                      .build()
      );
      messagePage = new PageImpl<>(messages, PAGEABLE, messages.size());

      userChat.setLastReadMessage(createMessage(createdOn));
    }

    @Test
    void findReadMessages_shouldReturnNotEmptyList_whenProvidedMessage() {
      when(messageRepository.findById(FROM_MESSAGE_ID)).thenReturn(Optional.of(messages.get(0)));
      when(messageRepository.findAllByChatIdAndCreationDateLessThan(CHAT_ID, messages.get(0).getCreationDate(), PAGEABLE))
              .thenReturn(messagePage);
      setupMessageMapper();

      Page<MessageDto> result = underTest.findReadMessages(CHAT_ID, Optional.of(FROM_MESSAGE_ID), PAGEABLE);
      assertMessageDtoResponse(result);

      verify(messageRepository).findAllByChatIdAndCreationDateLessThan(anyLong(), any(ZonedDateTime.class), any(Pageable.class));
      verifyNoInteractions(authenticationService);
      verify(messageRepository, never()).findAllByChatId(anyLong(), any(Pageable.class));
      verify(messageRepository, never()).findAllByChatIdAndCreationDateLessThanEqual(anyLong(), any(ZonedDateTime.class), any(Pageable.class));
    }

    @Test
    void findReadMessages_shouldReturnNotEmptyList_whenNotProvidedMessage() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository.findByChatIdAndUserId(CHAT_ID, user.getId())).thenReturn(
          Optional.of(userChat));
      when(messageRepository.findAllByChatIdAndCreationDateLessThanEqual(CHAT_ID, messages.get(0).getCreationDate(), PAGEABLE))
          .thenReturn(messagePage);
      setupMessageMapper();

      Page<MessageDto> result = underTest.findReadMessages(CHAT_ID, Optional.empty(), PAGEABLE);
      assertMessageDtoResponse(result);

      verify(messageRepository).findAllByChatIdAndCreationDateLessThanEqual(anyLong(), any(ZonedDateTime.class), any(Pageable.class));
      verify(messageRepository, never()).findAllByChatId(anyLong(), any(Pageable.class));
      verify(messageRepository, never()).findAllByChatIdAndCreationDateLessThan(anyLong(), any(ZonedDateTime.class), any(Pageable.class));
    }

    @Test
    void findReadMessages_shouldReturnNotEmptyList_whenNotProvidedMessageAndNoLastReadMessages() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository.findByChatIdAndUserId(CHAT_ID, user.getId())).thenReturn(
          Optional.of(new UserChat()));
      when(messageRepository.findAllByChatId(CHAT_ID, PAGEABLE)).thenReturn(messagePage);
      setupMessageMapper();

      Page<MessageDto> result = underTest.findReadMessages(CHAT_ID, Optional.empty(), PAGEABLE);
      assertMessageDtoResponse(result);

      verify(messageRepository).findAllByChatId(CHAT_ID, PAGEABLE);
      verify(messageRepository, never()).findAllByChatIdAndCreationDateLessThan(anyLong(), any(ZonedDateTime.class), any(Pageable.class));
      verify(messageRepository, never()).findAllByChatIdAndCreationDateLessThanEqual(anyLong(), any(ZonedDateTime.class), any(Pageable.class));
    }

    private void setupMessageMapper() {
      messages.forEach(message ->
          when(messageMapper.toMessageDto(message)).thenReturn(new MessageDto(message.getContent()))
      );
    }

    private void assertMessageDtoResponse(Page<MessageDto> result) {
      assertEquals(2, result.getTotalElements());
      assertEquals("message 1", result.toList().get(0).content());
      assertEquals("message 2", result.toList().get(1).content());
    }

    @Test
    void findReadMessages_shouldThrowNotFound_whenNoSpecifiedMessageFound() {
      when(messageRepository.findById(FROM_MESSAGE_ID)).thenReturn(Optional.empty());
      assertThrows(MessageNotFoundException.class ,() -> underTest.findReadMessages(CHAT_ID,
          Optional.of(FROM_MESSAGE_ID), PAGEABLE));
    }

    @Test
    void findUnreadMessages_shouldReturnNotEmptyList_whenMessagesFound() {
      when(authenticationService.getAuthenticatedUser()).thenReturn(user);
      when(userChatRepository.findByChatIdAndUserId(CHAT_ID, user.getId())).thenReturn(Optional.of(userChat));
      when(messageRepository.findAllByChatIdAndCreationDateAfter(CHAT_ID, createdOn, PAGEABLE))
              .thenReturn(messagePage);

      when(messageMapper.toMessageDto(messages.get(0))).thenReturn(new MessageDto(messages.get(0).getContent()));
      when(messageMapper.toMessageDto(messages.get(1))).thenReturn(new MessageDto(messages.get(1).getContent()));

      Page<MessageDto> result = underTest.findUnreadMessages(CHAT_ID, PAGEABLE);

      assertEquals(messages.get(0).getContent(), result.toList().get(0).content());
      assertEquals(messages.get(1).getContent(), result.toList().get(1).content());
    }

    private Message createMessage(ZonedDateTime creationDate) {
      return Message.builder()
              .id(1L)
              .content("Last read message")
              .creationDate(creationDate)
              .build();
    }
  }

}
