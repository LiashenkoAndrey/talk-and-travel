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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.exception.chat.PrivateChatAlreadyExistsException;
import online.talkandtravel.exception.message.MessageFromAnotherChatException;
import online.talkandtravel.exception.message.MessageNotFoundException;
import online.talkandtravel.exception.model.HttpException;
import online.talkandtravel.exception.user.TheSameUserException;
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

    private static final User bob = getBob(), alice = getAlice(), tomas = getTomas();
    private static final User removedUser = User.builder()
        .id(null)
        .userName("user left the chat")
        .about("user left the chat")
        .userEmail("undefined")
        .build();

    @ParameterizedTest
    @MethodSource("findAllUsersPrivateChatsArgs")
    void testCompanions_shouldReturnListRelativeToArguments(User authenticatedUser,
        int expectedSize, Map<Long, User> companionsMap) {
      testAuthenticationService.authenticateUser(authenticatedUser);

      List<PrivateChatDto> actualList = underTest.findAllUsersPrivateChats();
      assertNotNull(actualList);
      assertEquals(expectedSize, actualList.size());

      if (companionsMap == null) return;

      for (Entry<Long, User> longUserEntry : companionsMap.entrySet()) {
        Optional<PrivateChatDto> optionalPrivateChatDto = actualList.stream()
            .filter((e) -> e.chat().id().equals(longUserEntry.getKey())).findFirst();
        assertTrue(optionalPrivateChatDto.isPresent(),
            "Expected chat with id " + longUserEntry.getKey() + " not found for user: " + authenticatedUser.getUserName());
        PrivateChatDto chatDto = optionalPrivateChatDto.get();
        UserDtoShort actual = chatDto.companion();
        User expected = longUserEntry.getValue();

        assertEquals(expected.getId(), actual.id());
        assertEquals(expected.getUserName(), chatDto.chat().name());
      }
    }

    private static Stream<Arguments> findAllUsersPrivateChatsArgs() {
      return Stream.of(
          Arguments.of(tomas, 0, null),
          Arguments.of(alice, 2,
              Map.of(ALICE_BOB_PRIVATE_CHAT_ID, bob, ALICE_DELETED_USER_PRIVATE_CHAT_ID,
                  removedUser)),
          Arguments.of(bob, 1, Map.of(ALICE_BOB_PRIVATE_CHAT_ID, alice))
      );
    }

    @ParameterizedTest
    @MethodSource("testUnreadMessagesAndLastMessageArgs")
    void testUnreadMessagesAndLastMessage(User authenticatedUser,
        List<ExpectedData> expectedDataList) {
      testAuthenticationService.authenticateUser(authenticatedUser);
      expectedDataList.stream()
          .filter(Objects::nonNull)
          .forEach((data) -> testChatService.setLastReadMessageId(data.getChatId(), authenticatedUser.getId(), data.getLastReadMessageId()));

      List<PrivateChatDto> actualList = underTest.findAllUsersPrivateChats();
      assertNotNull(actualList);
      assertEquals(expectedDataList.size(), actualList.size());

      IntStream.range(0, actualList.size()).forEach((i) -> {
        PrivateChatDto actual = actualList.get(i);
        ExpectedData expectedData = expectedDataList.get(i);

        assertEquals(expectedData.unreadMessagesCount, actual.chat().unreadMessagesCount());
        if (expectedData.lastMessageId == null) {
          assertNull(actual.lastMessage());
        } else {
          assertEquals(expectedData.lastMessageId, actual.lastMessage().id());
        }
      });
    }

    private static Stream<Arguments> testUnreadMessagesAndLastMessageArgs() {
      return Stream.of(
          Arguments.of(alice,
              List.of(
                  new ExpectedData(ALICE_BOB_PRIVATE_CHAT_ID, 4L, 6L, 10L),
                  new ExpectedData(ALICE_DELETED_USER_PRIVATE_CHAT_ID, 101L, 2L, 103L))),
          Arguments.of(bob, List.of(
              new ExpectedData(ALICE_BOB_PRIVATE_CHAT_ID, null, 0L, 10L)
          )));
    }

    @Getter
    @AllArgsConstructor
    private static class ExpectedData {
      private Long chatId, lastReadMessageId, unreadMessagesCount, lastMessageId;
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
    void findReadMessages_shouldReturnAllMessages_whenFromMessageIdNotPresent() {
//      testAuthenticationService.authenticateUser(alice);

//      Page<MessageDto> messageDtoPage = underTest.findReadMessages(CHAT_ID, 1L, Pageable.unpaged());
      assertThrows(MessageNotFoundException.class, () -> underTest.findReadMessages(CHAT_ID, 500L, Pageable.unpaged()));
//      assertEquals(10, messageDtoPage.getSize());
    }

    @Test
    void findReadMessages_shouldReturnFiveMessages_whenLastReadIsPresent() {
      testAuthenticationService.authenticateUser(alice);
      Long lastReadMessageId = 5L;
      testChatService.setLastReadMessageId(CHAT_ID, getAlice().getId(), lastReadMessageId);

      Page<MessageDto> messageDtoPage = underTest.findReadMessages(CHAT_ID, 1L, Pageable.unpaged());
      assertEquals(5, messageDtoPage.getSize());

      messageDtoPage.getContent().forEach(
          (message) -> assertThat(message.id()).isLessThanOrEqualTo(lastReadMessageId));
    }

    @Test
    void findUnreadMessages_shouldReturnAllMessages_whenLastReadNotPresent() {
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
      assertThrows(TheSameUserException.class,
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
