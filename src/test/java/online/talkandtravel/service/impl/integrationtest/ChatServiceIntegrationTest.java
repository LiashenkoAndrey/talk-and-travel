package online.talkandtravel.service.impl.integrationtest;

import static online.talkandtravel.config.TestDataConstant.CHAT_MESSAGES_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.PRIVATE_CHATS_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static online.talkandtravel.testdata.UserTestData.getTomas;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.exception.chat.PrivateChatAlreadyExistsException;
import online.talkandtravel.exception.user.UserChatNotFoundException;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatDto;
import online.talkandtravel.model.dto.chat.SetLastReadMessageRequest;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.util.TestAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@Log4j2
@Sql({USERS_DATA_SQL, PRIVATE_CHATS_DATA_SQL, CHAT_MESSAGES_DATA_SQL})
public class ChatServiceIntegrationTest extends IntegrationTest {

  @Autowired
  private ChatService underTest;

  @Autowired
  private ChatRepository chatRepository;

  @Autowired
  private UserChatRepository userChatRepository;

  @Autowired
  private TestAuthenticationService testAuthenticationService;

  private User bob, alice;

  @BeforeEach
  void init() {
    bob = getBob();
    alice = getAlice();
  }


  @Nested
  class FindAllUsersPrivateChats {
    @ParameterizedTest
    @MethodSource("findAllUsersPrivateChatsArgs")
    void shouldReturnListRelativeToArguments(User auhenticatedUser,
        Long expectedSize, User companion) {
      testAuthenticationService.authenticateUser(auhenticatedUser);

      List<PrivateChatDto> actualList = underTest.findAllUsersPrivateChats();

      assertEquals(expectedSize, actualList.size());

      if (companion != null) {
        PrivateChatDto chat = actualList.get(0);
        assertEquals(companion.getId(), chat.companion().getId());
        assertEquals(companion.getUserName(), chat.chat().name());
      }
    }

    private static Stream<Arguments> findAllUsersPrivateChatsArgs() {
      User bob = getBob(), alice = getAlice(), tomas = getTomas();
      return Stream.of(
          Arguments.of(tomas, 0L, null),
          Arguments.of(alice, 1L, bob),
          Arguments.of(bob, 1L, alice)
      );
    }
  }

  @Nested
  class SetLastReadMessage {

    @ParameterizedTest
    @MethodSource("shouldThrow_whenNoChatFoundArgs")
    void shouldThrow_whenNoChatFound(User auhenticatedUser, Long chatId) {
      testAuthenticationService.authenticateUser(auhenticatedUser);
      SetLastReadMessageRequest request = new SetLastReadMessageRequest(4L);
      assertThrows(UserChatNotFoundException.class, () -> underTest.setLastReadMessage(chatId, request));
    }

    private static Stream<Arguments> shouldThrow_whenNoChatFoundArgs() {
      User alice = getAlice(), tomas = getTomas();
      return Stream.of(
          Arguments.of(tomas, 10000L),
          Arguments.of(alice, 10001L)
      );
    }

    @Test
    void shouldUpdate_whenChatFound() {
      testAuthenticationService.authenticateUser(alice);
      Long chatId = 10000L, lastReadMessageId = 4L;
      SetLastReadMessageRequest request = new SetLastReadMessageRequest(lastReadMessageId);

      assertNull(getCurrentLastReadMessageId(chatId));

      underTest.setLastReadMessage(chatId, request);

      assertEquals(lastReadMessageId, getCurrentLastReadMessageId(chatId));
    }

    private Long getCurrentLastReadMessageId(Long chatId) {
      UserChat userChat = userChatRepository.findByChatIdAndUserId(chatId, alice.getId()).orElseThrow(() -> new UserChatNotFoundException(chatId, alice.getId()));
      return userChat.getLastReadMessageId();
    }
  }

  @Nested
  class FindMessages {

    private static final Long CHAT_ID = 10000L;

    @Test
    void findReadMessages_shouldReturnAllMessages_whenLastReadNotPresent() {
      testAuthenticationService.authenticateUser(alice);

      Page<MessageDto> messageDtoPage = underTest.findReadMessages(CHAT_ID, Pageable.unpaged());
      assertEquals(10, messageDtoPage.getSize());
    }

    @Test
    void findReadMessages_shouldReturnFiveMessages_whenLastReadIsPresent() {
      testAuthenticationService.authenticateUser(alice);
      Long lastReadMessageId = 5L;
      setLastRead(CHAT_ID, getAlice().getId(), lastReadMessageId);

      Page<MessageDto> messageDtoPage = underTest.findReadMessages(CHAT_ID, Pageable.unpaged());
      assertEquals(5, messageDtoPage.getSize());

      messageDtoPage.getContent().forEach(
          (message) -> assertThat(message.id()).isLessThanOrEqualTo(lastReadMessageId));
    }

    @Test
    void findUnReadMessages_shouldReturnAllMessages_whenLastReadNotPresent() {
      testAuthenticationService.authenticateUser(alice);

      Page<MessageDto> messageDtoPage = underTest.findUnreadMessages(CHAT_ID, Pageable.unpaged());
      assertEquals(0, messageDtoPage.getSize());
    }


    @Test
    void findUnreadMessages_shouldReturnFiveMessages_whenLastReadIsPresent() {
      testAuthenticationService.authenticateUser(alice);
      Long lastReadMessageId = 6L;
      setLastRead(CHAT_ID, getAlice().getId(), lastReadMessageId);

      Page<MessageDto> messageDtoPage = underTest.findUnreadMessages(CHAT_ID, Pageable.unpaged());
      assertEquals(4, messageDtoPage.getSize());

      messageDtoPage.getContent().forEach(
          (message) -> assertThat(message.id()).isGreaterThan(lastReadMessageId));
    }

    private void setLastRead(Long chatId, Long userId, Long lastReadMessageId) {
      UserChat userChat =
          userChatRepository
              .findByChatIdAndUserId(chatId, userId)
              .orElseThrow(() -> new UserChatNotFoundException(chatId, userId));
      userChat.setLastReadMessageId(lastReadMessageId);
      userChatRepository.save(userChat);
    }
  }

  @Nested
  class CreatePrivateChat {

    @Test
    void shouldThrow_whenCreateForTheSameUser() {
      testAuthenticationService.authenticateUser(bob);
      assertThrows(IllegalArgumentException.class,
          () -> underTest.createPrivateChat( new NewPrivateChatDto(bob.getId())));
    }

    @ParameterizedTest
    @MethodSource("whenChatExistsArgs")
    void shouldThrow_whenChatExists(User auhenticatedUser, User companion) {
      testAuthenticationService.authenticateUser(auhenticatedUser);
      assertThrows(PrivateChatAlreadyExistsException.class,
          () -> underTest.createPrivateChat( new NewPrivateChatDto(companion.getId())));
    }

    @ParameterizedTest
    @MethodSource("shouldCreatePrivateChatArgs")
    void should_createPrivateChat(User auhenticatedUser, User companion) {
      testAuthenticationService.authenticateUser(auhenticatedUser);

      Long chatId = underTest.createPrivateChat(new NewPrivateChatDto(companion.getId()));
      assertNotNull(chatId);
      assertTrue(chatRepository.existsById(chatId));
    }

    private static Stream<Arguments> shouldCreatePrivateChatArgs() {
      User alice = getAlice(), tomas = getTomas(), bob = getBob();
      return Stream.of(
          Arguments.of(alice, tomas),
          Arguments.of(tomas, bob)
      );
    }

    private static Stream<Arguments> whenChatExistsArgs() {
      return Stream.of(
          Arguments.of(getAlice(), getBob()),
          Arguments.of(getBob(), getAlice())
      );
    }
  }
}
