package online.talkandtravel.service.impl.integrationtest;

import static online.talkandtravel.config.TestDataConstant.CHAT_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static online.talkandtravel.testdata.ChatTestData.ANGOLA_CHAT_ID;
import static online.talkandtravel.testdata.ChatTestData.ARUBA_CHAT_ID;
import static online.talkandtravel.testdata.ChatTestData.EXISTING_MESSAGE_ID_OF_ARUBA_CHAT;
import static online.talkandtravel.testdata.ChatTestData.NOT_EXISTING_CHAT_ID;
import static online.talkandtravel.testdata.ChatTestData.REPLIED_MESSAGE_ID;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.Principal;
import java.util.stream.Stream;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.model.entity.MessageType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.MessageService;
import online.talkandtravel.util.TestAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql({USERS_DATA_SQL, CHAT_DATA_SQL})
public class MessageServiceIntegrationTest extends IntegrationTest {

  @Autowired
  private TestAuthenticationService testAuthenticationService;

  @Autowired
  private MessageService underTest;

  private User bob, alise;

  @BeforeEach
  void init() {
    bob = getBob();
    alise = getAlice();
  }

  @Test
  void saveMessage_shouldThrow_whenUserNotJoinedInChat() {
    Principal principal = testAuthenticationService.authenticateUser(bob);
    SendMessageRequest request = new SendMessageRequest("content", ARUBA_CHAT_ID, null);

    assertThrows(UserNotJoinedTheChatException.class, () -> underTest.saveMessage(request, principal));
  }

  @Test
  void saveMessage_shouldThrow_whenChatNotFound() {
    Principal principal = testAuthenticationService.authenticateUser(bob);
    SendMessageRequest request = new SendMessageRequest("content", NOT_EXISTING_CHAT_ID, null);

    assertThrows(UserNotJoinedTheChatException.class, () -> underTest.saveMessage(request, principal));
  }

  @Test
  void saveMessage_shouldThrow_whenRepliedMessageNotFound() {
    Principal principal = testAuthenticationService.authenticateUser(bob);
    SendMessageRequest request = new SendMessageRequest("content", ARUBA_CHAT_ID, REPLIED_MESSAGE_ID);

    assertThrows(WebSocketException.class, () -> underTest.saveMessage(request, principal));
  }

  @Test
  void saveMessage_shouldThrow_whenRepliedMessageFromAnotherChat() {
    Principal principal = testAuthenticationService.authenticateUser(bob);
    SendMessageRequest request = new SendMessageRequest("content", ANGOLA_CHAT_ID, EXISTING_MESSAGE_ID_OF_ARUBA_CHAT);

    assertThrows(WebSocketException.class, () -> underTest.saveMessage(request, principal));
  }

  @ParameterizedTest
  @MethodSource("saveMessageArgs")
  void saveMessage_shouldSave(User authUser, Long messageId, Long repliedMessageId) {
    Principal principal = testAuthenticationService.authenticateUser(authUser);
    String content = "content";
    SendMessageRequest request = new SendMessageRequest(content, ARUBA_CHAT_ID, repliedMessageId);

    MessageDto actual = underTest.saveMessage(request, principal);

    assertEquals(messageId, actual.id());
    assertEquals(content, actual.content());
    assertEquals(MessageType.TEXT, actual.type());
    assertNotNull(actual.creationDate());
    assertNull(actual.attachment());
    assertUserDataExists(actual);
    assertEquals(alise.getId(), actual.user().id());

    if (repliedMessageId != null) {
      assertNotNull(actual.repliedMessage());
      assertEquals(repliedMessageId, actual.repliedMessage().id());
    } else {
      assertNull(actual.repliedMessage());
    }
  }

  private void assertUserDataExists(MessageDto actual) {
    assertNotNull(actual.user());
    assertNotNull(actual.user().id());
    assertNotNull(actual.user().userName());
    assertNull(actual.user().avatar());
  }

  private static Stream<Arguments> saveMessageArgs() {
    return Stream.of(
        Arguments.of(getAlice(), 1L, null),
        Arguments.of(getAlice(), 2L, EXISTING_MESSAGE_ID_OF_ARUBA_CHAT)
    );
  }
}
