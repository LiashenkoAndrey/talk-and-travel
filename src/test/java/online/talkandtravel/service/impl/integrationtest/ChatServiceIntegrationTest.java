package online.talkandtravel.service.impl.integrationtest;

import static online.talkandtravel.config.TestDataConstant.CHAT_MESSAGES_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.PRIVATE_CHATS_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static online.talkandtravel.testdata.UserTestData.getTomas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.exception.user.UserChatNotFoundException;
import online.talkandtravel.model.dto.chat.PrivateChatDto;
import online.talkandtravel.model.dto.chat.SetLastReadMessageRequest;
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

  private User bob, alice, tomas;

  @BeforeEach
  void init() {
    bob = getBob();
    alice = getAlice();
    tomas = getTomas();
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
  class FindReadMessages {

  }
}