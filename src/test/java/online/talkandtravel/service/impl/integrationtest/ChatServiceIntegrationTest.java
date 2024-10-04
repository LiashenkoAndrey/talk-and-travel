package online.talkandtravel.service.impl.integrationtest;

import static online.talkandtravel.config.TestDataConstant.CHAT_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.CHAT_MESSAGES_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.PRIVATE_CHATS_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static online.talkandtravel.testdata.ChatTestData.ALICE_BOB_PRIVATE_CHAT_ID;
import static online.talkandtravel.testdata.ChatTestData.ALICE_DELETED_USER_PRIVATE_CHAT_ID;
import static online.talkandtravel.testdata.ChatTestData.ARUBA_CHAT_ID;
import static online.talkandtravel.testdata.ChatTestData.FIRST_MESSAGE_ID_OF_ALICE_BOB_CHAT;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.PrivateChatAlreadyExistsException;
import online.talkandtravel.exception.message.MessageFromAnotherChatException;
import online.talkandtravel.exception.model.HttpException;
import online.talkandtravel.exception.user.UserChatNotFoundException;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatDto;
import online.talkandtravel.model.dto.chat.SetLastReadMessageRequest;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.UserDtoShort;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.util.TestAuthenticationService;
import online.talkandtravel.util.TestChatService;
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
@Sql({USERS_DATA_SQL, PRIVATE_CHATS_DATA_SQL, CHAT_MESSAGES_DATA_SQL, CHAT_DATA_SQL})
public class ChatServiceIntegrationTest extends IntegrationTest {

  @Autowired private ChatService underTest;

  @Autowired private ChatRepository chatRepository;

  @Autowired private UserChatRepository userChatRepository;

  @Autowired private TestAuthenticationService testAuthenticationService;

  @Autowired private TestChatService testChatService;

  @Autowired private MessageRepository messageRepository;

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
        Long expectedSize, Map<Long, User> companionsMap) {
      testAuthenticationService.authenticateUser(auhenticatedUser);

      List<PrivateChatDto> actualList = underTest.findAllUsersPrivateChats();
      log.info("actualList {}", actualList);
      assertEquals(expectedSize, actualList.size());

      if (companionsMap == null) {
        return;
      }
      for (Entry<Long, User> longUserEntry : companionsMap.entrySet()) {
        Optional<PrivateChatDto> optionalPrivateChatDto = actualList.stream()
            .filter((e) -> e.chat().id().equals(longUserEntry.getKey())).findFirst();
        assertTrue(optionalPrivateChatDto.isPresent());
        PrivateChatDto chatDto = optionalPrivateChatDto.get();
        UserDtoShort actual = chatDto.companion();
        User expected = longUserEntry.getValue();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUserName(), chatDto.chat().name());
      }
    }

    private static Stream<Arguments> findAllUsersPrivateChatsArgs() {
      User bob = getBob(), alice = getAlice(), tomas = getTomas();
      User removedUser = User.builder()
          .id(null)
          .userName("user left the chat")
          .about("user left the chat")
          .userEmail("undefined")
          .build();
      return Stream.of(
          Arguments.of(tomas, 0L, null),
          Arguments.of(alice, 2L,
              Map.of(ALICE_BOB_PRIVATE_CHAT_ID, bob, ALICE_DELETED_USER_PRIVATE_CHAT_ID,
                  removedUser)),
          Arguments.of(bob, 1L, Map.of(ALICE_BOB_PRIVATE_CHAT_ID, alice))
      );
    }
  }

  @Nested
  class SetLastReadMessage {

    @ParameterizedTest
    @MethodSource("shouldThrow_whenNoChatFoundArgs")
    void shouldThrow_exception(Class<HttpException> httpExceptionClass, User auhenticatedUser, Long chatId, Long lastReadMessageId) {
      testAuthenticationService.authenticateUser(auhenticatedUser);
      SetLastReadMessageRequest request = new SetLastReadMessageRequest(lastReadMessageId);
      assertThrows(httpExceptionClass, () -> underTest.setLastReadMessage(chatId, request));
    }

    private static Stream<Arguments> shouldThrow_whenNoChatFoundArgs() {
      User alice = getAlice(), tomas = getTomas();
      return Stream.of(
          Arguments.of(UserChatNotFoundException.class, tomas, 77777L, 4L),
          Arguments.of(UserChatNotFoundException.class, alice, 77772L, 4L),
          Arguments.of(MessageFromAnotherChatException.class, alice, ARUBA_CHAT_ID, FIRST_MESSAGE_ID_OF_ALICE_BOB_CHAT)
      );
    }

    @ParameterizedTest
    @MethodSource("shouldUpdate_whenChatFoundArgs")
    void shouldUpdate_whenChatFound(User authUser, Long lastReadMessageId,
        Long expectedUnreadMessagesCount) {
      Long chatId = ALICE_BOB_PRIVATE_CHAT_ID;
      testAuthenticationService.authenticateUser(authUser);

      SetLastReadMessageRequest request = new SetLastReadMessageRequest(lastReadMessageId);
      assertNull(getUserChat(chatId).getLastReadMessage());

      underTest.setLastReadMessage(chatId, request);

      UserChat userChat = getUserChat(chatId);

      assertEquals(lastReadMessageId, userChat.getLastReadMessage().getId());
      assertEquals(expectedUnreadMessagesCount,
          messageRepository.countAllByChatIdAndCreationDateAfter(chatId, userChat.getLastReadMessage()
              .getCreationDate()));
    }

    private static Stream<Arguments> shouldUpdate_whenChatFoundArgs() {
      return Stream.of(
          Arguments.of(getAlice(), 4L, 6L),
          Arguments.of(getAlice(), 1L, 9L),
          Arguments.of(getAlice(), 10L, 0L)
      );
    }

    private UserChat getUserChat(Long chatId) {
      return userChatRepository.findByChatIdAndUserId(chatId, alice.getId())
          .orElseThrow(() -> new UserChatNotFoundException(chatId, alice.getId()));
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
      testChatService.setLastReadMessageId(CHAT_ID, getAlice().getId(), lastReadMessageId);

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
      testChatService.setLastReadMessageId(CHAT_ID, getAlice().getId(), lastReadMessageId);

      Page<MessageDto> messageDtoPage = underTest.findUnreadMessages(CHAT_ID, Pageable.unpaged());
      assertEquals(4, messageDtoPage.getSize());

      messageDtoPage.getContent().forEach(
          (message) -> assertThat(message.id()).isGreaterThan(lastReadMessageId));
    }
  }

  @Nested
  class CreatePrivateChat {

    @Test
    void shouldThrow_whenCreateForTheSameUser() {
      testAuthenticationService.authenticateUser(bob);
      assertThrows(IllegalArgumentException.class,
          () -> underTest.createPrivateChat(new NewPrivateChatDto(bob.getId())));
    }

    @ParameterizedTest
    @MethodSource("whenChatExistsArgs")
    void shouldThrow_whenChatExists(User auhenticatedUser, User companion) {
      testAuthenticationService.authenticateUser(auhenticatedUser);
      assertThrows(PrivateChatAlreadyExistsException.class,
          () -> underTest.createPrivateChat(new NewPrivateChatDto(companion.getId())));
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
